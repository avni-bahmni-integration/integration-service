package org.bahmni_avni_integration.integration_data.internal;

public final class SubjectToPatientMetaData {
    private final String subjectType;
    private final String avniIdentifierConcept;
    private final String encounterTypeUuid;
    private final String subjectUuidConceptUuid;

    public SubjectToPatientMetaData(String subjectType, String avniIdentifierConcept, String encounterTypeUuid, String subjectUuidConceptUuid) {
        this.subjectType = subjectType;
        this.avniIdentifierConcept = avniIdentifierConcept;
        this.encounterTypeUuid = encounterTypeUuid;
        this.subjectUuidConceptUuid = subjectUuidConceptUuid;
    }

    public String subjectType() {
        return subjectType;
    }

    public String avniIdentifierConcept() {
        return avniIdentifierConcept;
    }

    public String encounterTypeUuid() {
        return encounterTypeUuid;
    }

    public String subjectUuidConceptUuid() {
        return subjectUuidConceptUuid;
    }
}