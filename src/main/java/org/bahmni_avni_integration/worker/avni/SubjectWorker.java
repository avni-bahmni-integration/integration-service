package org.bahmni_avni_integration.worker.avni;

import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSPatient;
import org.bahmni_avni_integration.domain.*;
import org.bahmni_avni_integration.mapper.avni.SubjectMapper;
import org.bahmni_avni_integration.repository.AvniEntityStatusRepository;
import org.bahmni_avni_integration.repository.MappingMetaDataRepository;
import org.bahmni_avni_integration.repository.avni.AvniSubjectRepository;
import org.bahmni_avni_integration.repository.openmrs.OpenMRSEncounterRepository;
import org.bahmni_avni_integration.repository.openmrs.OpenMRSPatientRepository;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.function.Predicate;

@Component
public class SubjectWorker {
    @Autowired
    private AvniEntityStatusRepository avniEntityStatusRepository;
    @Autowired
    private MappingMetaDataRepository mappingMetaDataRepository;
    @Autowired
    private OpenMRSEncounterRepository openMRSEncounterRepository;
    @Autowired
    private AvniSubjectRepository avniSubjectRepository;
    @Autowired
    private SubjectMapper subjectMapper;
    @Autowired
    private OpenMRSPatientRepository patientRepository;

    public void processSubjects(Constants constants, Predicate<Subject[]> continueAfterOneRecord) {
        AvniEntityStatus status = avniEntityStatusRepository.findByEntityType(AvniEntityType.Subject);
        MappingMetaData patientSubjectMapping = mappingMetaDataRepository.findByMappingGroupAndMappingType(MappingGroup.PatientSubject, MappingType.Patient_SubjectType);
        MappingMetaData patientIdentifierMapping = mappingMetaDataRepository.findByMappingGroupAndMappingType(MappingGroup.PatientSubject, MappingType.PatientIdentifier_Concept);
        String encounterTypeUuid = mappingMetaDataRepository.getBahmniValue(MappingGroup.PatientSubject, MappingType.Subject_EncounterType);

        while (true) {
            Subject[] subjects = avniSubjectRepository.getSubjects(status.getReadUpto(), patientSubjectMapping.getAvniValue());
            if (subjects.length == 0) break;
            for (Subject subject : subjects) {
                String subjectId = (String) subject.get("ID");
                String patientIdentifier = constants.getValue(ConstantKey.BahmniIdentifierPrefix) + (String) subject.getObservation(patientIdentifierMapping.getAvniValue());
                Pair<OpenMRSPatient, OpenMRSEncounter> patientEncounter = openMRSEncounterRepository.getEncounter(patientIdentifier, subjectId);
                OpenMRSPatient patient = patientEncounter.getValue0();
                OpenMRSEncounter encounter = patientEncounter.getValue1();
                if (encounter == null) {
                    if (patient != null) {
                        encounter = subjectMapper.mapSubjectToEncounter(subject, patient.getUuid(), encounterTypeUuid, constants);
                        openMRSEncounterRepository.createEncounter(encounter);
                        status.setReadUpto(subject.getLastModifiedDate());
                        avniEntityStatusRepository.save(status);
                    }
                }
                if (!continueAfterOneRecord.test(subjects)) break;
            }
            
        }
    }

    public void processSubjects(Constants constants) {
        this.processSubjects(constants, subjects -> true);
    }
}