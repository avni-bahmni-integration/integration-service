package org.bahmni_avni_integration.worker.avni;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSPatient;
import org.bahmni_avni_integration.contract.internal.SubjectToPatientMetaData;
import org.bahmni_avni_integration.domain.*;
import org.bahmni_avni_integration.repository.AvniEntityStatusRepository;
import org.bahmni_avni_integration.repository.avni.AvniSubjectRepository;
import org.bahmni_avni_integration.service.EntityStatusService;
import org.bahmni_avni_integration.service.MappingMetaDataService;
import org.bahmni_avni_integration.service.PatientService;
import org.bahmni_avni_integration.util.ObjectJsonMapper;
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

    private static Logger logger = Logger.getLogger(SubjectWorker.class);

    public void processSubjects(Constants constants, Predicate<Subject> continueAfterOneRecord) {
        SubjectToPatientMetaData metaData = mappingMetaDataService.getForSubjectToPatient();

        while (true) {
            AvniEntityStatus status = avniEntityStatusRepository.findByEntityType(AvniEntityType.Subject);
            Subject[] subjects = avniSubjectRepository.getSubjects(status.getReadUpto(), metaData.subjectType());
            logger.info(String.format("Found %d subjects that are newer than %s", subjects.length, status.getReadUpto()));
            if (subjects.length == 0) break;
            for (Subject subject : subjects) {
                if (processSubject(constants, continueAfterOneRecord, metaData, subject)) break;
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected boolean processSubject(Constants constants, Predicate<Subject> continueAfterOneRecord, SubjectToPatientMetaData metaData, Subject subject) {
        Pair<OpenMRSPatient, OpenMRSEncounter> patientEncounter = patientService.findSubject(subject, constants, metaData);
        OpenMRSPatient patient = patientEncounter.getValue0();
        OpenMRSEncounter encounter = patientEncounter.getValue1();

        if (encounter != null && patient != null) {
            patientService.updateSubject(patient, subject, metaData, constants);
        } else if (encounter != null && patient == null) {
            // todo: openmrs doesn't support the ability to find encounter without providing the patient hence this condition will never be reached
            patientService.processPatientIdChanged(subject, metaData);
        } else if (encounter == null && patient != null) {
            patientService.createSubject(subject, patient, metaData, constants);
        } else if (encounter == null && patient == null) {
            patientService.processPatientNotFound(subject, metaData);
        }
        entityStatusService.saveEntityStatus(subject);

        if (!continueAfterOneRecord.test(subject)) return true;
        return false;
    }

    public void processSubjects(Constants constants) {
        this.processSubjects(constants, subjects -> true);
    }
}