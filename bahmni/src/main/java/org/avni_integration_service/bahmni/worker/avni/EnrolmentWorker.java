package org.avni_integration_service.bahmni.worker.avni;

import org.apache.log4j.Logger;
import org.avni_integration_service.bahmni.service.*;
import org.avni_integration_service.contract.avni.Enrolment;
import org.avni_integration_service.contract.avni.EnrolmentsResponse;
import org.avni_integration_service.contract.avni.Subject;
import org.avni_integration_service.bahmni.contract.OpenMRSPatient;
import org.avni_integration_service.integration_data.domain.AvniEntityStatus;
import org.avni_integration_service.integration_data.domain.AvniEntityType;
import org.avni_integration_service.integration_data.domain.Constants;
import org.avni_integration_service.integration_data.domain.ErrorType;
import org.avni_integration_service.bahmni.SubjectToPatientMetaData;
import org.avni_integration_service.integration_data.repository.AvniEntityStatusRepository;
import org.avni_integration_service.contract.repository.AvniEnrolmentRepository;
import org.avni_integration_service.contract.repository.AvniIgnoredConceptsRepository;
import org.avni_integration_service.contract.repository.AvniSubjectRepository;
import org.avni_integration_service.bahmni.worker.ErrorRecordWorker;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;

@Component
public class EnrolmentWorker implements ErrorRecordWorker {
    private final AvniEntityStatusRepository avniEntityStatusRepository;
    private final AvniEnrolmentRepository avniEnrolmentRepository;
    private final EntityStatusService entityStatusService;
    private final EnrolmentService enrolmentService;
    private final PatientService patientService;
    private final ErrorService errorService;
    private final AvniIgnoredConceptsRepository avniIgnoredConceptsRepository;

    private static final Logger logger = Logger.getLogger(EnrolmentWorker.class);
    private final AvniSubjectRepository avniSubjectRepository;
    private final MappingMetaDataService mappingMetaDataService;
    private SubjectToPatientMetaData metaData;
    private Constants constants;

    public EnrolmentWorker(AvniEntityStatusRepository avniEntityStatusRepository,
                           MappingMetaDataService mappingMetaDataService,
                           AvniEnrolmentRepository avniEnrolmentRepository,
                           EntityStatusService entityStatusService,
                           EnrolmentService enrolmentService,
                           PatientService patientService,
                           ErrorService errorService,
                           AvniIgnoredConceptsRepository avniIgnoredConceptsRepository, AvniSubjectRepository avniSubjectRepository) {
        this.avniEntityStatusRepository = avniEntityStatusRepository;
        this.mappingMetaDataService = mappingMetaDataService;
        this.avniEnrolmentRepository = avniEnrolmentRepository;
        this.entityStatusService = entityStatusService;
        this.enrolmentService = enrolmentService;
        this.patientService = patientService;
        this.errorService = errorService;
        this.avniIgnoredConceptsRepository = avniIgnoredConceptsRepository;
        this.avniSubjectRepository = avniSubjectRepository;
    }

    public void processEnrolments() {
        while (true) {
            AvniEntityStatus status = avniEntityStatusRepository.findByEntityType(AvniEntityType.Enrolment);
            EnrolmentsResponse response = avniEnrolmentRepository.getEnrolments(status.getReadUpto());
            int totalPages = response.getTotalPages();
            Enrolment[] enrolments = response.getContent();
            logger.info(String.format("Found %d enrolments that are newer than %s", enrolments.length, status.getReadUpto()));
            if (enrolments.length == 0) break;
            for (Enrolment enrolment : enrolments) {
                processEnrolment(enrolment, true);
            }
            if (totalPages == 1) {
                logger.info("Finished processing all pages");
                break;
            }
        }
    }

    private void removeIgnoredObservations(Enrolment enrolment) {
        var observations = (LinkedHashMap<String, Object>) enrolment.get("observations");
        var exitObservations = (LinkedHashMap<String, Object>) enrolment.get("exitObservations");
        for (var ignoredConcept : avniIgnoredConceptsRepository.getIgnoredConcepts()) {
            observations.remove(ignoredConcept);
            exitObservations.remove(ignoredConcept);
        }
        enrolment.set("observations", observations);
        enrolment.set("exitObservations", exitObservations);
    }

    private void updateSyncStatus(Enrolment enrolment, boolean updateSyncStatus) {
        if (updateSyncStatus)
            entityStatusService.saveEntityStatus(enrolment);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void processEnrolment(Enrolment enrolment, boolean updateSyncStatus) {
        if (errorService.hasAvniMultipleSubjectsError(enrolment.getSubjectId())) {
            logger.error(String.format("Skipping Avni enrolment %s because of multiple subjects with same id error", enrolment.getUuid()));
            errorService.errorOccurred(enrolment, ErrorType.MultipleSubjectsWithId);
            updateSyncStatus(enrolment, updateSyncStatus);
            return;
        }
        removeIgnoredObservations(enrolment);
        logger.debug(String.format("Processing avni %s enrolment %s", enrolment.getProgram(), enrolment.getUuid()));
        Subject subject = avniSubjectRepository.getSubject(enrolment.getSubjectId());
        logger.debug(String.format("Found avni subject %s", subject.getUuid()));
        if (subject.getVoided()) {
            logger.debug(String.format("Avni subject is voided. Skipping. %s", subject.getUuid()));
            updateSyncStatus(enrolment, updateSyncStatus);
            return;
        }

        var patient = patientService.findPatient(subject, constants, metaData);
        if (patient == null) {
            logger.debug(String.format("Patient with identifier %s not found", subject.getId(metaData.avniIdentifierConcept())));
            enrolmentService.processPatientNotFound(enrolment);
        } else {
            var encounter = enrolmentService.findCommunityEnrolment(enrolment, patient);
            if (encounter == null) {
                logger.debug(String.format("Creating new Bahmni enrolment for Avni enrolment %s", enrolment.getUuid()));
                enrolmentService.createCommunityEnrolment(enrolment, patient, constants);
            } else {
                logger.debug(String.format("Updating existing Bahmni enrolment encounter %s", encounter.getUuid()));
                enrolmentService.updateCommunityEnrolment(encounter, enrolment, constants);
            }
            if (enrolment.isExited()) {
                processExitedEnrolment(constants, enrolment, patient);
            }
            errorService.successfullyProcessed(enrolment);
        }

        updateSyncStatus(enrolment, updateSyncStatus);
    }

    private void processExitedEnrolment(Constants constants, Enrolment enrolment, OpenMRSPatient patient) {
        var exitEncounter = enrolmentService.findCommunityExitEnrolment(enrolment, patient);
        if (exitEncounter == null) {
            logger.debug(String.format("Creating new Bahmni exit enrolment for Avni enrolment %s", enrolment.getUuid()));
            enrolmentService.createCommunityExitEnrolment(enrolment, patient, constants);
        } else {
            logger.debug(String.format("Updating existing Bahmni exit enrolment Encounter %s", exitEncounter.getUuid()));
            enrolmentService.updateCommunityExitEnrolment(exitEncounter, enrolment, constants);
        }
    }

    @Override
    public void processError(String entityUuid) {
        Enrolment enrolment = avniEnrolmentRepository.getEnrolment(entityUuid);
        if (enrolment == null) {
            logger.warn(String.format("Enrolment has been deleted now: %s", entityUuid));
            errorService.errorOccurred(entityUuid, ErrorType.EntityIsDeleted, AvniEntityType.Enrolment);
            return;
        }

        processEnrolment(enrolment, false);
    }

    public void cacheRunImmutables(Constants constants) {
        this.constants = constants;
        metaData = mappingMetaDataService.getForSubjectToPatient();
    }
}
