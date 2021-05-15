package org.bahmni_avni_integration.worker.avni;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.contract.avni.SubjectsResponse;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSFullEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSPatient;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSUuidHolder;
import org.bahmni_avni_integration.integration_data.domain.*;
import org.bahmni_avni_integration.integration_data.internal.SubjectToPatientMetaData;
import org.bahmni_avni_integration.integration_data.repository.AvniEntityStatusRepository;
import org.bahmni_avni_integration.integration_data.repository.MultipleResultsFoundException;
import org.bahmni_avni_integration.integration_data.repository.avni.AvniIgnoredConceptsRepository;
import org.bahmni_avni_integration.integration_data.repository.avni.AvniSubjectRepository;
import org.bahmni_avni_integration.service.EntityStatusService;
import org.bahmni_avni_integration.service.ErrorService;
import org.bahmni_avni_integration.service.MappingMetaDataService;
import org.bahmni_avni_integration.service.PatientService;
import org.bahmni_avni_integration.worker.ErrorRecordWorker;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;

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

    private static final Logger logger = Logger.getLogger(SubjectWorker.class);
    private SubjectToPatientMetaData metaData;
    private Constants constants;

    public void processSubjects() {
        while (true) {
            AvniEntityStatus status = avniEntityStatusRepository.findByEntityType(AvniEntityType.Subject);
            SubjectsResponse response = avniSubjectRepository.getSubjects(status.getReadUpto(), constants.getValue(ConstantKey.IntegrationAvniSubjectType));
            Subject[] subjects = response.getContent();
            int totalElements = response.getTotalElements();
            int totalPages = response.getTotalPages();
            logger.info(String.format("Found %d subjects that are newer than %s", subjects.length, status.getReadUpto()));
            if (subjects.length == 0) break;
            for (Subject subject : subjects) {
                processSubject(subject);
            }
            if (totalElements == 1 && totalPages == 1) break;
        }
    }

    private void removeIgnoredObservations(Subject subject) {
        var observations = (LinkedHashMap<String, Object>) subject.get("observations");
        avniIgnoredConceptsRepository.getIgnoredConcepts().forEach(observations::remove);
        subject.set("observations", observations);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void processSubject(Subject subject) {
        removeIgnoredObservations(subject);
        Pair<OpenMRSPatient, OpenMRSFullEncounter> patientEncounter;
        try {
            patientEncounter = patientService.findSubject(subject, constants, metaData);
        } catch (MultipleResultsFoundException e) {
            patientService.processMultipleSubjectsFound(subject);
            return;
        }

        OpenMRSPatient patient = patientEncounter.getValue0();
        OpenMRSFullEncounter encounter = patientEncounter.getValue1();

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
            logger.debug(String.format("Creating new patient for subject %s", subject.getUuid()));
            patient = patientService.createPatient(subject, metaData, constants);
            logger.debug(String.format("Creating new encounter for subject %s", subject.getUuid()));
            patientService.createSubject(subject, patient, metaData, constants);
        }
        entityStatusService.saveEntityStatus(subject);
    }

    @Override
    public void processError(String entityUuid) {
        Subject subject = avniSubjectRepository.getSubject(entityUuid);
        if (subject == null) {
            logger.warn(String.format("Subject has been deleted now: %s", entityUuid));
            errorService.errorOccurred(entityUuid, ErrorType.EntityIsDeleted, AvniEntityType.Subject);
            return;
        }

        processSubject(subject);
    }

    public void cacheRunImmutables(Constants constants) {
        this.constants = constants;
        metaData = mappingMetaDataService.getForSubjectToPatient();
    }
}