package org.bahmni_avni_integration.contract.internal;

public record PatientToSubjectMetaData(String patientUuidConcept, String subjectType, String avniIdentifierConcept,
                                       String patientEncounterType, String patientIdentifierName) {}