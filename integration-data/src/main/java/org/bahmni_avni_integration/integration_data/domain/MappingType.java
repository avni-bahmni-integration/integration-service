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
    AvniRegistrationDate_Concept(13),
    AvniEnrolmentDate_Concept(14),
    AvniExitDate_Concept(15),
    AvniEncounterDate_Concept(16),
    AvniProgramData_Concept(17),
    BahmniUUID_Concept(18),
    BahmniForm_CommunityProgram(19),
    CommunityRegistration_BahmniForm(20),
    CommunityEnrolment_BahmniForm(21),
    CommunityEnrolmentExit_BahmniForm(22),
    CommunityProgramEncounter_BahmniForm(23),
    CommunityEncounter_BahmniForm(24),
    PatientIdentifier_Concept(25);

    private final int value;
    MappingType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
