package org.avni_integration_service.service;

import org.avni_integration_service.integration_data.domain.*;
import org.avni_integration_service.integration_data.internal.BahmniEncounterToAvniEncounterMetaData;
import org.avni_integration_service.integration_data.internal.PatientToSubjectMetaData;
import org.avni_integration_service.integration_data.internal.SubjectToPatientMetaData;
import org.avni_integration_service.integration_data.repository.IgnoredBahmniConceptRepository;
import org.avni_integration_service.integration_data.repository.MappingMetaDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class MappingMetaDataService {
    @Autowired
    private MappingMetaDataRepository mappingMetaDataRepository;
    @Autowired
    private IgnoredBahmniConceptRepository ignoredBahmniConceptRepository;

    private static final MappingType[] bahmniEncounterMappingTypes = {MappingType.EncounterType, MappingType.DrugOrderEncounterType, MappingType.LabEncounterType};

    public SubjectToPatientMetaData getForSubjectToPatient() {
        MappingMetaData patientIdentifierMapping = mappingMetaDataRepository.findByMappingGroupAndMappingType(MappingGroup.PatientSubject, MappingType.PatientIdentifier_Concept);
        String avniIdentifierConcept = patientIdentifierMapping.getAvniValue();

        String encounterTypeUuid = mappingMetaDataRepository.getBahmniValue(MappingGroup.PatientSubject, MappingType.Subject_EncounterType);

        String subjectUuidConceptUuid = mappingMetaDataRepository.getBahmniValueForAvniIdConcept();

        return new SubjectToPatientMetaData(avniIdentifierConcept, encounterTypeUuid, subjectUuidConceptUuid);
    }

    public PatientToSubjectMetaData getForPatientToSubject() {
        String avniIdentifierConcept = mappingMetaDataRepository.getAvniValue(MappingGroup.PatientSubject, MappingType.PatientIdentifier_Concept);
        String patientEncounterType = Names.AvniPatientRegistrationEncounter;
        String patientIdentifierName = mappingMetaDataRepository.getBahmniValue(MappingGroup.PatientSubject, MappingType.PatientIdentifier_Concept);
        String bahmniEntityUuidConceptInAvni = mappingMetaDataRepository.getAvniValue(MappingGroup.Common, MappingType.BahmniUUID_Concept);
        return new PatientToSubjectMetaData(bahmniEntityUuidConceptInAvni, avniIdentifierConcept, patientEncounterType, patientIdentifierName);
    }

    public BahmniEncounterToAvniEncounterMetaData getForBahmniEncounterToAvniEntities() {
        List<MappingMetaData> mappings = mappingMetaDataRepository.findAllByMappingType(MappingType.EncounterType);
        BahmniEncounterToAvniEncounterMetaData metaData = new BahmniEncounterToAvniEncounterMetaData();
        metaData.addEncounterMappings(mappings);

        String bahmniEntityUuidConceptInAvni = mappingMetaDataRepository.getAvniValue(MappingGroup.Common, MappingType.BahmniUUID_Concept);
        metaData.setBahmniEntityUuidConcept(bahmniEntityUuidConceptInAvni);

        metaData.addLabMapping(mappingMetaDataRepository.findByMappingType(MappingType.LabEncounterType));
        metaData.addDrugOrderMapping(mappingMetaDataRepository.findByMappingType(MappingType.DrugOrderEncounterType));
        metaData.addDrugOrderConceptMapping(mappingMetaDataRepository.findByMappingType(MappingType.DrugOrderConcept));
        metaData.addProgramMapping(mappingMetaDataRepository.findAllByMappingGroupAndMappingType(MappingGroup.ProgramEnrolment, MappingType.BahmniForm_CommunityProgram));
        ArrayList<IgnoredBahmniConcept> ignoredBahmniConcepts = new ArrayList<>();
        ignoredBahmniConceptRepository.findAll().forEach(ignoredBahmniConcepts::add);
        metaData.setIgnoredConcepts(ignoredBahmniConcepts);
        return metaData;
    }

    public boolean isBahmniEncounterInAvni(String encounterType) {
        return mappingMetaDataRepository.findAllByMappingTypeInAndAvniValue(Arrays.asList(bahmniEncounterMappingTypes), encounterType).size() != 0;
    }
}
