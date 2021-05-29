package org.bahmni_avni_integration.integration_data.domain;

public enum MappingType implements BaseEnum {
    Subject_EncounterType(1),
    Concept(2),
    PersonAttributeConcept(3),
    EncounterType(4),
    LabEncounterType(5),
    DrugOrderEncounterType(6),
    DrugOrderConcept(7),
    CommunityEnrolment_EncounterType(8),
    CommunityEnrolmentExit_EncounterType(9),
    CommunityProgramEncounter_EncounterType(10),
    CommunityEncounter_EncounterType(11),
    AvniUUID_Concept(12),
    AvniEventDate_Concept(13),
    AvniProgramData_Concept(14),
    BahmniUUID_Concept(15),
    BahmniForm_CommunityProgram(16),
    CommunityRegistration_BahmniForm(17),
    CommunityEnrolment_BahmniForm(18),
    CommunityEnrolmentExit_BahmniForm(19),
    CommunityProgramEncounter_BahmniForm(20),
    CommunityEncounter_BahmniForm(21),
    PatientIdentifier_Concept(22);

    private final int value;
    MappingType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
