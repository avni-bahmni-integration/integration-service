package org.bahmni_avni_integration.migrator.domain;

public class UserProvidedConceptName implements OpenMRSTerminology {
    private final String conceptName;

    public UserProvidedConceptName(String conceptName) {
        this.conceptName = conceptName;
    }

    @Override
    public String getUuid() {
        throw new RuntimeException("UUID should not be required");
    }

    @Override
    public String getAvniName() {
        return conceptName;
    }
}