package org.avni_integration_service.migrator.domain;

public class OpenMRSConceptName implements OpenMRSTerminology {
    private final String conceptName;

    public OpenMRSConceptName(String conceptName) {
        this.conceptName = conceptName;
    }

    @Override
    public String getUuid() {
        throw new RuntimeException("UUID should not be required");
    }

    @Override
    public String getAvniName() {
        return NameMapping.fromBahmniConceptToAvni(conceptName);
    }
}
