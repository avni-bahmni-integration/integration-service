package org.bahmni_avni_integration.migrator.domain;

public class ConceptName implements OpenMRSTerminology {
    private String conceptName;

    public ConceptName(String conceptName) {
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