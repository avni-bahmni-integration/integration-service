package org.bahmni_avni_integration.worker.avni;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSFullEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSUuidHolder;
import org.bahmni_avni_integration.integration_data.internal.SubjectToPatientMetaData;
import org.bahmni_avni_integration.integration_data.domain.AvniEntityStatus;
import org.bahmni_avni_integration.integration_data.domain.AvniEntityType;
import org.bahmni_avni_integration.integration_data.domain.Constants;
import org.bahmni_avni_integration.integration_data.repository.AvniEntityStatusRepository;
import org.bahmni_avni_integration.integration_data.repository.avni.AvniSubjectRepository;
import org.bahmni_avni_integration.service.EntityStatusService;
import org.bahmni_avni_integration.service.MappingMetaDataService;
import org.bahmni_avni_integration.service.PatientService;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Predicate;

@Component
public class SubjectWorker {
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

    private static final Logger logger = Logger.getLogger(SubjectWorker.class);

    public void processSubjects(Constants constants, Predicate<Subject> continueAfterOneRecord) {
        SubjectToPatientMetaData metaData = mappingMetaDataService.getForSubjectToPatient();

        mainLoop:
        while (true) {
            AvniEntityStatus status = avniEntityStatusRepository.findByEntityType(AvniEntityType.Subject);
            Subject[] subjects = avniSubjectRepository.getSubjects(status.getReadUpto(), metaData.subjectType());
            logger.info(String.format("Found %d subjects that are newer than %s", subjects.length, status.getReadUpto()));
            if (subjects.length == 0) break;
            for (Subject subject : subjects) {
                if (processSubject(constants, continueAfterOneRecord, metaData, subject)) break mainLoop;
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected boolean processSubject(Constants constants, Predicate<Subject> continueAfterOneRecord, SubjectToPatientMetaData metaData, Subject subject) {
        Pair<OpenMRSUuidHolder, OpenMRSFullEncounter> patientEncounter = patientService.findSubject(subject, constants, metaData);
        OpenMRSUuidHolder patient = patientEncounter.getValue0();
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
            patientService.createPatient(subject, metaData, constants);
        }
        entityStatusService.saveEntityStatus(subject);

        return !continueAfterOneRecord.test(subject);
    }

    public void processSubjects(Constants constants) {
        this.processSubjects(constants, subjects -> true);
    }
}