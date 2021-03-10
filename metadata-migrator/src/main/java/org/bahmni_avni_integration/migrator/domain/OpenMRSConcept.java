package org.bahmni_avni_integration.migrator.domain;

public class OpenMRSConcept {
    private String uuid;
    private String name;

    public OpenMRSConcept() {
    }

    public OpenMRSConcept(String uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public String getAvniConceptName() {
        return String.format("%s [Bahmni]", getName());
    }

    public void setName(String name) {
        this.name = name;
    }
}