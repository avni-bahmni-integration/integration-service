package org.avni_integration_service.integration_data.internal;

public final class SubjectToPatientMetaData {
    private final String avniIdentifierConcept;
    private final String encounterTypeUuid;
    private final String subjectUuidConceptUuid;

    public SubjectToPatientMetaData(String avniIdentifierConcept, String encounterTypeUuid, String subjectUuidConceptUuid) {
        this.avniIdentifierConcept = avniIdentifierConcept;
        this.encounterTypeUuid = encounterTypeUuid;
        this.subjectUuidConceptUuid = subjectUuidConceptUuid;
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
