package org.avni_integration_service.worker.avni;

import org.apache.log4j.Logger;
import org.avni_integration_service.contract.avni.Subject;
import org.avni_integration_service.contract.avni.SubjectsResponse;
import org.avni_integration_service.bahmni.contract.OpenMRSFullEncounter;
import org.avni_integration_service.bahmni.contract.OpenMRSPatient;
import org.avni_integration_service.integration_data.domain.*;
import org.avni_integration_service.integration_data.internal.SubjectToPatientMetaData;
import org.avni_integration_service.integration_data.repository.AvniEntityStatusRepository;
import org.avni_integration_service.integration_data.repository.avni.AvniIgnoredConceptsRepository;
import org.avni_integration_service.integration_data.repository.avni.AvniSubjectRepository;
import org.avni_integration_service.service.*;
import org.avni_integration_service.worker.ErrorRecordWorker;
import org.avni_integration_service.worker.bahmni.atomfeedworker.PatientEncounterEventWorker;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@Component
public class SubjectWorker implements ErrorRecordWorker {
    @Autowired
    private AvniEntityStatusRepository avniEntityStatusRepository;
    @Autowired
    private MappingMetaDataService mappingMetaDataService;
    @Autowired
    private AvniSubjectRepository avniSubjectRepository;
    @Autowired
    private PatientService patientService;
    @Autowired
    private EntityStatusService entityStatusService;
    @Autowired
    private ErrorService errorService;
    @Autowired
    private AvniIgnoredConceptsRepository avniIgnoredConceptsRepository;
    @Autowired
    private SubjectService subjectService;

    private static final Logger logger = Logger.getLogger(SubjectWorker.class);
    private SubjectToPatientMetaData metaData;
    private Constants constants;

    public void processSubjects() {
        while (true) {
            AvniEntityStatus status = avniEntityStatusRepository.findByEntityType(AvniEntityType.Subject);
            SubjectsResponse response = avniSubjectRepository.getSubjects(status.getReadUpto(), constants.getValue(ConstantKey.IntegrationAvniSubjectType));
            Subject[] subjects = response.getContent();
            int totalPages = response.getTotalPages();
            logger.info(String.format("Found %d subjects that are newer than %s", subjects.length, status.getReadUpto()));
            if (subjects.length == 0) break;
            for (Subject subject : subjects) {
                processSubject(subject, true);
            }
            if (totalPages == 1) {
                logger.info("Finished processing all pages");
                break;
            }
        }
    }

    private void removeIgnoredObservations(Subject subject) {
        var observations = (LinkedHashMap<String, Object>) subject.get("observations");
        avniIgnoredConceptsRepository.getIgnoredConcepts().forEach(observations::remove);
        subject.set("observations", observations);
    }

    private void updateSyncStatus(Subject subject, boolean updateSyncStatus) {
        if (updateSyncStatus)
            entityStatusService.saveEntityStatus(subject);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void processSubject(Subject subject, boolean updateSyncStatus) {
        logger.debug("Processing subject %s".formatted(subject.getUuid()));

        if(subject.getId(metaData) == null) {
            logger.debug("Skip subject %s because of having null identifier".formatted(subject.getUuid()));
            patientService.processSubjectIdNull(subject);
            updateSyncStatus(subject, updateSyncStatus);
            return;
        }

        if (hasDuplicates(subject)) {
            if(!subject.getVoided()) {
                logger.error("Create multiple subjects found error for subject %s identifier %s".formatted(subject.getUuid(), subject.getId(metaData)));
                patientService.processMultipleSubjectsFound(subject);
            } else {
                logger.debug("Skip voided subject %s because of having non voided duplicates".formatted(subject.getUuid()));
            }
            updateSyncStatus(subject, updateSyncStatus);
            return;
        };
        removeIgnoredObservations(subject);

        try {
            Pair<OpenMRSPatient, OpenMRSFullEncounter> patientEncounter = patientService.findSubject(subject, constants, metaData);
            var patient = patientEncounter.getValue0();
            var encounter = patientEncounter.getValue1();

            if (encounter != null && patient != null) {
                logger.debug(String.format("Updating existing encounter %s for subject %s", encounter.getUuid(), subject.getUuid()));
                patientService.updateSubject(encounter, patient, subject, metaData, constants);
            } else if (encounter != null && patient == null) {
                // product-roadmap-todo: openmrs doesn't support the ability to find encounter without providing the patient hence this condition will never be reached
                patientService.processPatientIdChanged(subject, metaData);
            } else if (encounter == null && patient != null) {
                logger.debug(String.format("Creating new encounter for subject %s", subject.getUuid()));
                patientService.createSubject(subject, patient, metaData, constants);
            } else if (encounter == null && patient == null) {
                logger.debug(String.format("Creating new patient and new encounter for subject %s", subject.getUuid()));
                patientService.createPatientAndSubject(subject, metaData, constants);
            }
            logger.debug(String.format("Saving entity status for subject %s", subject.getLastModifiedDate()));
        } catch (PatientEncounterEventWorker.SubjectIdChangedException e) {
            errorService.errorOccurred(subject.getUuid(), ErrorType.SubjectIdChanged, AvniEntityType.Subject);
        }

        updateSyncStatus(subject, updateSyncStatus);
    }

    private boolean hasDuplicates(Subject subject) {
        Subject[] subjects = subjectService.findSubjects(subject, metaData, constants);
        int sizeOfNonVoidedOtherThanSelf = Arrays.stream(subjects)
                .filter(s -> !s.getUuid().equals(subject.getUuid()))
                .filter(s -> !s.getVoided())
                .collect(Collectors.toList())
                .size();
        boolean hasDuplicates = sizeOfNonVoidedOtherThanSelf > 0;
        if (hasDuplicates) {
            logger.debug("Duplicate subjects found for subject %s identifier %s voided %s".formatted(subject.getUuid(),
                    subject.getId(metaData),
                    subject.getVoided()));
        }
        return hasDuplicates;
    }

    @Override
    public void processError(String entityUuid) {
        Subject subject = avniSubjectRepository.getSubject(entityUuid);
        if (subject == null) {
            logger.warn(String.format("Subject has been deleted now: %s", entityUuid));
            errorService.errorOccurred(entityUuid, ErrorType.EntityIsDeleted, AvniEntityType.Subject);
            return;
        }

        processSubject(subject, false);
    }

    public void cacheRunImmutables(Constants constants) {
        this.constants = constants;
        metaData = mappingMetaDataService.getForSubjectToPatient();
    }
}
