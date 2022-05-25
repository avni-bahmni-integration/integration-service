package org.avni_integration_service.bahmni.service;

import org.avni_integration_service.bahmni.*;
import org.avni_integration_service.bahmni.repository.intmapping.MappingService;
import org.avni_integration_service.integration_data.domain.*;
import org.avni_integration_service.integration_data.repository.IgnoredIntegratingConceptRepository;
import org.avni_integration_service.integration_data.repository.MappingMetaDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class MappingMetaDataService {
    @Autowired
    private MappingService mappingService;
    @Autowired
    private MappingMetaDataRepository mappingMetaDataRepository;
    @Autowired
    private IgnoredIntegratingConceptRepository ignoredBahmniConceptRepository;

    private static final MappingType[] bahmniEncounterMappingTypes = {BahmniMappingType.EncounterType, BahmniMappingType.DrugOrderEncounterType, BahmniMappingType.LabEncounterType};

    public SubjectToPatientMetaData getForSubjectToPatient() {
        MappingMetaData patientIdentifierMapping = mappingService.findByMappingGroupAndMappingType(BahmniMappingGroup.PatientSubject, BahmniMappingType.PatientIdentifier_Concept);
        String avniIdentifierConcept = patientIdentifierMapping.getAvniValue();

        String encounterTypeUuid = mappingService.getBahmniValue(BahmniMappingGroup.PatientSubject, BahmniMappingType.Subject_EncounterType);

        String subjectUuidConceptUuid = mappingService.getBahmniValueForAvniIdConcept();

        return new SubjectToPatientMetaData(avniIdentifierConcept, encounterTypeUuid, subjectUuidConceptUuid);
    }

    public PatientToSubjectMetaData getForPatientToSubject() {
        String avniIdentifierConcept = mappingService.getAvniValue(BahmniMappingGroup.PatientSubject, BahmniMappingType.PatientIdentifier_Concept);
        String patientEncounterType = Names.AvniPatientRegistrationEncounter;
        String patientIdentifierName = mappingService.getBahmniValue(BahmniMappingGroup.PatientSubject, BahmniMappingType.PatientIdentifier_Concept);
        String bahmniEntityUuidConceptInAvni = mappingService.getAvniValue(BahmniMappingGroup.Common, BahmniMappingType.BahmniUUID_Concept);
        return new PatientToSubjectMetaData(bahmniEntityUuidConceptInAvni, avniIdentifierConcept, patientEncounterType, patientIdentifierName);
    }

    public BahmniEncounterToAvniEncounterMetaData getForBahmniEncounterToAvniEntities() {
        List<MappingMetaData> mappings = mappingMetaDataRepository.findAllByMappingType(BahmniMappingType.EncounterType);
        BahmniEncounterToAvniEncounterMetaData metaData = new BahmniEncounterToAvniEncounterMetaData();
        metaData.addEncounterMappings(mappings);

        String bahmniEntityUuidConceptInAvni = mappingService.getAvniValue(BahmniMappingGroup.Common, BahmniMappingType.BahmniUUID_Concept);
        metaData.setBahmniEntityUuidConcept(bahmniEntityUuidConceptInAvni);

        metaData.addLabMapping(mappingMetaDataRepository.findByMappingType(BahmniMappingType.LabEncounterType));
        metaData.addDrugOrderMapping(mappingMetaDataRepository.findByMappingType(BahmniMappingType.DrugOrderEncounterType));
        metaData.addDrugOrderConceptMapping(mappingMetaDataRepository.findByMappingType(BahmniMappingType.DrugOrderConcept));
        metaData.addProgramMapping(mappingMetaDataRepository.findAllByMappingGroupAndMappingType(BahmniMappingGroup.ProgramEnrolment, BahmniMappingType.BahmniForm_CommunityProgram));
        ArrayList<IgnoredIntegratingConcept> ignoredIntegratingConcepts = new ArrayList<>();
        ignoredBahmniConceptRepository.findAll().forEach(ignoredIntegratingConcepts::add);
        metaData.setIgnoredConcepts(ignoredIntegratingConcepts);
        return metaData;
    }

    public boolean isBahmniEncounterInAvni(String encounterType) {
        return mappingMetaDataRepository.findAllByMappingTypeInAndAvniValue(Arrays.asList(bahmniEncounterMappingTypes), encounterType).size() != 0;
    }
}
