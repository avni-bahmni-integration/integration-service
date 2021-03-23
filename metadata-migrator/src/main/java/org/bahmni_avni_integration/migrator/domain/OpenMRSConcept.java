package org.bahmni_avni_integration.migrator.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OpenMRSConcept {
    private String uuid;
    private String name;
    private String dataType;
    private String nameType;
    private List<OpenMRSConcept> answers = new ArrayList<>();

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

    public String getAvniConceptName() {
        return NameMapping.fromBahmniToAvni(getName());
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

    public List<OpenMRSConcept> getAnswers() {
        return answers;
    }

    public String getNameType() {
        return nameType;
    }

    public static List<OpenMRSConcept> getFullyQualifiedConceptsWherePresent(List<OpenMRSConcept> concepts) {
        Map<String, List<OpenMRSConcept>> groupedConcepts = concepts.stream().collect(Collectors.groupingBy(OpenMRSConcept::getUuid));
        ArrayList<OpenMRSConcept> uniqueConcepts = new ArrayList<>();
        for (String conceptUuid : groupedConcepts.keySet()) {
            List<OpenMRSConcept> conceptsWithSameUuid = groupedConcepts.get(conceptUuid);
            OpenMRSConcept concept;
            if (conceptsWithSameUuid.size() > 1)
                concept = concepts.stream().filter(openMRSConcept -> openMRSConcept.getNameType().equals("FULLY_QUALIFIED")).findFirst().orElse(concepts.stream().findFirst().orElse(null));
            else
                concept = concepts.get(0);
            uniqueConcepts.add(concept);
        }
        return uniqueConcepts;
    }
}