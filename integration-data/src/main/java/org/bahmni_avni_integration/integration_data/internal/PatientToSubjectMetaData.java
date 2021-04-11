package org.bahmni_avni_integration.integration_data.internal;

public final class PatientToSubjectMetaData implements BahmniToAvniMetaData {
    private final String bahmniEntityUuidConcept;
    private final String avniIdentifierConcept;
    private final String patientEncounterType;
    private final String patientIdentifierName;

    public PatientToSubjectMetaData(String bahmniEntityUuidConcept, String avniIdentifierConcept,
                                    String patientEncounterType, String patientIdentifierName) {
        this.bahmniEntityUuidConcept = bahmniEntityUuidConcept;
        this.avniIdentifierConcept = avniIdentifierConcept;
        this.patientEncounterType = patientEncounterType;
        this.patientIdentifierName = patientIdentifierName;
    }

    @Override
    public String getBahmniEntityUuidConcept() {
        return bahmniEntityUuidConcept;
    }

    public String bahmniEntityUuidConcept() {
        return bahmniEntityUuidConcept;
    }

    public String avniIdentifierConcept() {
        return avniIdentifierConcept;
    }

    public String patientEncounterType() {
        return patientEncounterType;
    }

    public String patientIdentifierName() {
        return patientIdentifierName;
    }

}