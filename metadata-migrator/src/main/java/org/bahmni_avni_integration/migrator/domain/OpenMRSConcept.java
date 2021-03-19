package org.bahmni_avni_integration.migrator.domain;

import java.util.List;

public class OpenMRSConcept {
    private String uuid;
    private String name;
    private String dataType;
    private String nameType;
    private List<OpenMRSConcept> answers;

    public OpenMRSConcept() {
    }

    public OpenMRSConcept(String uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public OpenMRSConcept(String uuid, String name, String dataType, String nameType) {
        this.uuid = uuid;
        this.name = name;
        this.dataType = dataType;
        this.nameType = nameType;
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

    public String getDataType() {
        return dataType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OpenMRSConcept that = (OpenMRSConcept) o;

        if (!uuid.equals(that.uuid)) return false;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = uuid.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    public void addAnswer(OpenMRSConcept openMRSConcept) {
        answers.add(openMRSConcept);
    }
}