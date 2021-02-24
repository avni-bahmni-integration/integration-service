package org.bahmni_avni_integration.service;

import org.bahmni_avni_integration.contract.internal.BahmniEncounterToAvniEncounterMetaData;
import org.bahmni_avni_integration.contract.internal.PatientToSubjectMetaData;
import org.bahmni_avni_integration.contract.internal.SubjectToPatientMetaData;
import org.bahmni_avni_integration.domain.MappingGroup;
import org.bahmni_avni_integration.domain.MappingMetaData;
import org.bahmni_avni_integration.domain.MappingType;
import org.bahmni_avni_integration.repository.MappingMetaDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MappingMetaDataService {
    @Autowired
    private MappingMetaDataRepository mappingMetaDataRepository;

    public SubjectToPatientMetaData getForSubjectToPatient() {
        MappingMetaData patientSubjectMapping = mappingMetaDataRepository.findByMappingGroupAndMappingType(MappingGroup.PatientSubject, MappingType.Patient_SubjectType);
        String subjectType = patientSubjectMapping.getAvniValue();

        MappingMetaData patientIdentifierMapping = mappingMetaDataRepository.findByMappingGroupAndMappingType(MappingGroup.PatientSubject, MappingType.PatientIdentifier_Concept);
        String avniIdentifierConcept = patientIdentifierMapping.getAvniValue();

        String encounterTypeUuid = mappingMetaDataRepository.getBahmniValue(MappingGroup.PatientSubject, MappingType.Subject_EncounterType);

        String subjectUuidConceptUuid = mappingMetaDataRepository.getBahmniValue(MappingGroup.PatientSubject, MappingType.SubjectUUID_Concept);

        return new SubjectToPatientMetaData(subjectType, avniIdentifierConcept, encounterTypeUuid, subjectUuidConceptUuid);
    }

    public PatientToSubjectMetaData getForPatientToSubject() {
        String bahmniEntityUuidConcept = mappingMetaDataRepository.getAvniValue(MappingGroup.Common, MappingType.BahmniUUID_Concept);
        String avniIdentifierConcept = mappingMetaDataRepository.getAvniValue(MappingGroup.PatientSubject, MappingType.PatientIdentifier_Concept);
        String subjectType = mappingMetaDataRepository.getAvniValue(MappingGroup.PatientSubject, MappingType.Patient_SubjectType);
        String patientEncounterType = mappingMetaDataRepository.getAvniValue(MappingGroup.PatientSubject, MappingType.Patient_EncounterType);
        String patientIdentifierName = mappingMetaDataRepository.getBahmniValue(MappingGroup.PatientSubject, MappingType.PatientIdentifier_Concept);
        return new PatientToSubjectMetaData(bahmniEntityUuidConcept, subjectType, avniIdentifierConcept, patientEncounterType, patientIdentifierName);
    }

    public BahmniEncounterToAvniEncounterMetaData getForBahmniEncounterToAvni() {
        List<MappingMetaData> mappings = mappingMetaDataRepository.findAllByMappingGroupAndMappingType(MappingGroup.GeneralEncounter, MappingType.EncounterType);
        BahmniEncounterToAvniEncounterMetaData metaData = new BahmniEncounterToAvniEncounterMetaData();
        mappings.forEach(x -> metaData.addEncounterType(x.getBahmniValue(), x.getAvniValue()));
        String patientUuidConcept = mappingMetaDataRepository.getAvniValue(MappingGroup.Common, MappingType.BahmniUUID_Concept);
        metaData.setBahmniEntityUuidConcept(patientUuidConcept);
//        mappingMetaDataRepository.findBy
        return metaData;
    }
}