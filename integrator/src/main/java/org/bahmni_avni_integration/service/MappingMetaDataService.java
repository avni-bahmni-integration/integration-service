package org.bahmni_avni_integration.service;

import org.bahmni_avni_integration.integration_data.internal.AvniToBahmniEnrolmentMetaData;
import org.bahmni_avni_integration.integration_data.internal.BahmniEncounterToAvniEncounterMetaData;
import org.bahmni_avni_integration.integration_data.internal.PatientToSubjectMetaData;
import org.bahmni_avni_integration.integration_data.internal.SubjectToPatientMetaData;
import org.bahmni_avni_integration.integration_data.domain.MappingGroup;
import org.bahmni_avni_integration.integration_data.domain.MappingMetaData;
import org.bahmni_avni_integration.integration_data.domain.MappingType;
import org.bahmni_avni_integration.integration_data.repository.MappingMetaDataRepository;
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

    public BahmniEncounterToAvniEncounterMetaData getForBahmniEncounterToAvniEncounter() {
        List<MappingMetaData> mappings = mappingMetaDataRepository.findAllByMappingType(MappingType.EncounterType);
        BahmniEncounterToAvniEncounterMetaData metaData = new BahmniEncounterToAvniEncounterMetaData();
        metaData.addEncounterMappings(mappings);
        String patientUuidConcept = mappingMetaDataRepository.getAvniValue(MappingGroup.Common, MappingType.BahmniUUID_Concept);
        metaData.setBahmniEntityUuidConcept(patientUuidConcept);
        return metaData;
    }

    public AvniToBahmniEnrolmentMetaData getForAvniToBahmniEnrolment() {
        AvniToBahmniEnrolmentMetaData metaData = new AvniToBahmniEnrolmentMetaData();
        String avniUuidConcept = mappingMetaDataRepository.getAvniValue(MappingGroup.Common, MappingType.AvniUUID_Concept);
        metaData.setAvniEntityUuidConcept(avniUuidConcept);
        return metaData;
    }
}