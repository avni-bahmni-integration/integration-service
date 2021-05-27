package org.bahmni_avni_integration.integration_data.domain;

public enum MappingGroup implements BaseEnum {
    Common(1), PatientSubject(2), GeneralEncounter(3), ProgramEnrolment(4), ProgramEncounter(5), Observation(6);

    private final int value;
    MappingGroup(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
