package org.bahmni_avni_integration.service;

import org.bahmni_avni_integration.contract.internal.SubjectToPatientMetaData;
import org.bahmni_avni_integration.domain.MappingGroup;
import org.bahmni_avni_integration.domain.MappingMetaData;
import org.bahmni_avni_integration.domain.MappingType;
import org.bahmni_avni_integration.repository.MappingMetaDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}