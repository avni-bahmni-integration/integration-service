package org.bahmni_avni_integration.worker.avni;

import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.bahmni_avni_integration.contract.avni.Enrolment;
import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSUuidHolder;
import org.bahmni_avni_integration.integration_data.domain.AvniEntityStatus;
import org.bahmni_avni_integration.integration_data.domain.AvniEntityType;
import org.bahmni_avni_integration.integration_data.domain.Constants;
import org.bahmni_avni_integration.integration_data.internal.SubjectToPatientMetaData;
import org.bahmni_avni_integration.integration_data.repository.AvniEntityStatusRepository;
import org.bahmni_avni_integration.integration_data.repository.avni.AvniEnrolmentRepository;
import org.bahmni_avni_integration.integration_data.repository.avni.AvniSubjectRepository;
import org.bahmni_avni_integration.service.*;
import org.bahmni_avni_integration.worker.ErrorRecordWorker;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Predicate;

@Component
public class EnrolmentWorker implements ErrorRecordWorker {
    private final AvniEntityStatusRepository avniEntityStatusRepository;
    private final AvniEnrolmentRepository avniEnrolmentRepository;
    private final EntityStatusService entityStatusService;
    private final EnrolmentService enrolmentService;
    private final PatientService patientService;
    private final ErrorService errorService;

    private static final Logger logger = Logger.getLogger(EnrolmentWorker.class);
    private final AvniSubjectRepository avniSubjectRepository;
    private final MappingMetaDataService mappingMetaDataService;

    public EnrolmentWorker(AvniEntityStatusRepository avniEntityStatusRepository,
                           MappingMetaDataService mappingMetaDataService,
                           AvniEnrolmentRepository avniEnrolmentRepository,
                           EntityStatusService entityStatusService,
                           EnrolmentService enrolmentService,
                           PatientService patientService,
                           ErrorService errorService,
                           AvniSubjectRepository avniSubjectRepository) {
        this.avniEntityStatusRepository = avniEntityStatusRepository;
        this.mappingMetaDataService = mappingMetaDataService;
        this.avniEnrolmentRepository = avniEnrolmentRepository;
        this.entityStatusService = entityStatusService;
        this.enrolmentService = enrolmentService;
        this.patientService = patientService;
        this.errorService = errorService;
        this.avniSubjectRepository = avniSubjectRepository;
    }

    public void processEnrolments(Constants constants, Predicate<Enrolment> continueAfterOneRecord) {
        SubjectToPatientMetaData subjectToPatientMetaData = mappingMetaDataService.getForSubjectToPatient();
        mainLoop:
        while (true) {
            AvniEntityStatus status = avniEntityStatusRepository.findByEntityType(AvniEntityType.Enrolment);
            Enrolment[] enrolments = avniEnrolmentRepository.getEnrolments(status.getReadUpto());
            logger.info(String.format("Found %d enrolments that are newer than %s", enrolments.length, status.getReadUpto()));
            if (enrolments.length == 0) break;
            for (Enrolment enrolment : enrolments) {
                if (processEnrolment(constants, continueAfterOneRecord, subjectToPatientMetaData, enrolment))
                    break mainLoop;
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected boolean processEnrolment(Constants constants, Predicate<Enrolment> continueAfterOneRecord, SubjectToPatientMetaData metaData, Enrolment enrolment) {
        logger.debug(String.format("Processing avni enrolment %s", enrolment.getUuid()));
        Subject subject = avniSubjectRepository.getSubject(enrolment.getSubjectId());
        logger.debug(String.format("Found avni subject %s", subject.getUuid()));

        var patient = patientService.findPatient(subject, constants, metaData);
        if (patient == null) {
            logger.debug(String.format("Patient with identifier %s not found", subject.getId(metaData)));
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

        entityStatusService.saveEntityStatus(enrolment);
        if (!continueAfterOneRecord.test(enrolment)) return true;
        return false;
    }

    private void processExitedEnrolment(Constants constants, Enrolment enrolment, OpenMRSUuidHolder patient) {
        var exitEncounter = enrolmentService.findCommunityExitEnrolment(enrolment, patient);
        if (exitEncounter == null) {
            logger.debug(String.format("Creating new Bahmni exit enrolment for Avni enrolment %s", enrolment.getUuid()));
            enrolmentService.createCommunityExitEnrolment(enrolment, patient, constants);
        } else {
            logger.debug(String.format("Updating existing Bahmni exit enrolment Encounter %s", exitEncounter.getUuid()));
            enrolmentService.updateCommunityExitEnrolment(exitEncounter, enrolment, constants);
        }
    }

    public void processEnrolments(Constants constants) {
        this.processEnrolments(constants, enrolment -> true);
    }

    @Override
    public void processError(String entityUuid) {
        throw new NotImplementedException();
    }
}