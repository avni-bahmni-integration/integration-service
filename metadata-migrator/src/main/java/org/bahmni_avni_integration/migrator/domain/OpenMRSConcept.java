package org.bahmni_avni_integration.migrator.domain;

import org.bahmni_avni_integration.integration_data.domain.ConstantKey;
import org.bahmni_avni_integration.integration_data.domain.Constants;
import org.bahmni_avni_integration.integration_data.domain.ObsDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class OpenMRSConcept implements OpenMRSTerminology {
    private String uuid;
    private String name;
    private String dataType;
    private String nameType;
    private List<OpenMRSConcept> answers = new ArrayList<>();

    public static final String FULLY_SPECIFIED = "FULLY_SPECIFIED";

    private OpenMRSConcept() {}

    public static OpenMRSConcept forFormExtract(String uuid, String name) {
        OpenMRSConcept openMRSConcept = new OpenMRSConcept();
        openMRSConcept.name = name;
        openMRSConcept.uuid = uuid;
        return openMRSConcept;
    }

    public static OpenMRSConcept forPersonConceptAndExtract(String uuid, String name, String dataType) {
        OpenMRSConcept openMRSConcept = new OpenMRSConcept();
        openMRSConcept.name = name;
        openMRSConcept.uuid = uuid;
        openMRSConcept.dataType = dataType;
        return openMRSConcept;
    }

    public static OpenMRSConcept forConceptExtract(String uuid, String name, String dataType, String nameType) {
        OpenMRSConcept openMRSConcept = new OpenMRSConcept();
        openMRSConcept.uuid = uuid;
        openMRSConcept.name = name;
        openMRSConcept.dataType = dataType;
        openMRSConcept.nameType = nameType;
        return openMRSConcept;
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

    public String getAvniName() {
        return NameMapping.fromBahmniConceptToAvni(getName());
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvniDataType() {
        if (dataType.equals(ObsDataType.Boolean.name())) return ObsDataType.Coded.name();
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

    public List<OpenMRSConcept> getAnswers() {
        return answers;
    }

    public List<OpenMRSConcept> getAvniAnswers(Map<String, Object> constants) {
        if (dataType.equals(ObsDataType.Boolean.name())) {
            return Arrays.asList(conceptForBoolean("True"), conceptForBoolean("False"));
        }
        return this.getAnswers();
    }

    private OpenMRSConcept conceptForBoolean(String openmrsConceptName) {
        OpenMRSConcept openMRSConcept = new OpenMRSConcept();
        openMRSConcept.dataType = ObsDataType.getAvniNADataType();
        openMRSConcept.name = openmrsConceptName;
        return openMRSConcept;
    }

    public String getNameType() {
        return nameType;
    }

    @Override
    public String toString() {
        return "{" +
                "name='" + name + '\'' +
                '}';
    }
}