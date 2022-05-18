package org.avni_integration_service.integration_data.domain;

import java.util.ArrayList;
import java.util.List;

public class MappingType implements BaseEnum {
    public static MappingType Subject_EncounterType = new MappingType(1, "Subject_EncounterType");
    public static MappingType Concept = new MappingType(2, "Concept");
    public static MappingType PersonAttributeConcept = new MappingType(3, "PersonAttributeConcept");
    public static MappingType EncounterType = new MappingType(4, "EncounterType");
    public static MappingType LabEncounterType = new MappingType(5, "LabEncounterType");
    public static MappingType DrugOrderEncounterType = new MappingType(6, "DrugOrderEncounterType");
    public static MappingType DrugOrderConcept = new MappingType(7, "DrugOrderConcept");
    public static MappingType CommunityEnrolment_EncounterType = new MappingType(8, "CommunityEnrolment_EncounterType");
    public static MappingType CommunityEnrolmentExit_EncounterType = new MappingType(9, "CommunityEnrolmentExit_EncounterType");
    public static MappingType CommunityProgramEncounter_EncounterType = new MappingType(10, "CommunityProgramEncounter_EncounterType");
    public static MappingType CommunityEncounter_EncounterType = new MappingType(11, "CommunityEncounter_EncounterType");
    public static MappingType AvniUUID_Concept = new MappingType(12, "AvniUUID_Concept");
    public static MappingType AvniEventDate_Concept = new MappingType(13, "AvniEventDate_Concept");
    public static MappingType AvniProgramData_Concept = new MappingType(14, "AvniProgramData_Concept");
    public static MappingType BahmniUUID_Concept = new MappingType(15, "BahmniUUID_Concept");
    public static MappingType BahmniForm_CommunityProgram = new MappingType(16, "BahmniForm_CommunityProgram");
    public static MappingType CommunityRegistration_BahmniForm = new MappingType(17, "CommunityRegistration_BahmniForm");
    public static MappingType CommunityEnrolment_BahmniForm = new MappingType(18, "CommunityEnrolment_BahmniForm");
    public static MappingType CommunityEnrolmentExit_BahmniForm = new MappingType(19, "CommunityEnrolmentExit_BahmniForm");
    public static MappingType CommunityProgramEncounter_BahmniForm = new MappingType(20, "CommunityProgramEncounter_BahmniForm");
    public static MappingType CommunityEncounter_BahmniForm = new MappingType(21, "CommunityEncounter_BahmniForm");
    public static MappingType PatientIdentifier_Concept = new MappingType(22, "PatientIdentifier_Concept");
    public static MappingType CommunityEnrolment_VisitType = new MappingType(23, "CommunityEnrolment_VisitType");
    public static MappingType AvniEventDate_VisitAttributeType = new MappingType(24, "AvniEventDate_VisitAttributeType");
    public static MappingType AvniUUID_VisitAttributeType = new MappingType(25, "AvniUUID_VisitAttributeType");

    private final int value;
    private final String name;

    MappingType(int value, String name) {
        this.value = value;
        this.name = name;
        AllMappingTypes.add(this);
    }

    public static BaseEnum[] values() {
        return AllMappingTypes.getAllMappingTypes().toArray(BaseEnum[]::new);
    }

    public static MappingType valueOf(String mappingType) {
        return AllMappingTypes.getAllMappingTypes().stream().filter(x -> x.name.equals(mappingType)).findFirst().orElse(null);
    }

    public static MappingType valueOf(int mappingValue) {
        return AllMappingTypes.getAllMappingTypes().stream().filter(x -> x.value == mappingValue).findFirst().orElse(null);
    }

    public int getValue() {
        return value;
    }

    @Override
    public String name() {
        return name;
    }
}
