package org.bahmni_avni_integration.worker.avni;

import org.bahmni_avni_integration.contract.avni.SubjectResponse;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSEncounter;
import org.bahmni_avni_integration.domain.*;
import org.bahmni_avni_integration.mapper.avni.SubjectMapper;
import org.bahmni_avni_integration.repository.AvniEntityStatusRepository;
import org.bahmni_avni_integration.repository.MappingMetaDataRepository;
import org.bahmni_avni_integration.repository.avni.AvniSubjectRepository;
import org.bahmni_avni_integration.repository.openmrs.OpenMRSEncounterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

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

    public void processSubjects() {
        AvniEntityStatus status = avniEntityStatusRepository.findByEntityType(AvniEntityType.Subject);
        MappingMetaData patientSubjectMapping = mappingMetaDataRepository.findByMappingGroupAndMappingType(MappingGroup.PatientSubject, MappingType.PatientSubjectType);
        MappingMetaData patientIdentifierMapping = mappingMetaDataRepository.findByMappingGroupAndMappingType(MappingGroup.PatientSubject, MappingType.PatientIdentifierConcept);
        SubjectResponse[] subjects = avniSubjectRepository.getSubjects(status.getReadUpto(), patientSubjectMapping.getAvniValue());
        SubjectMapper subjectMapper = new SubjectMapper();
        Arrays.stream(subjects).forEach(subject -> {
            String subjectId = (String) subject.get(patientIdentifierMapping.getAvniValue());
            OpenMRSEncounter encounter = openMRSEncounterRepository.getEncounter(subjectId, "Avni Entity UUID", subject.get("ID"));
            if (encounter == null) {
//                openMRSEncounterRepository.createEncounter();
            }
        });
    }
}