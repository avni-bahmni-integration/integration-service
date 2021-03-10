package org.bahmni_avni_integration.migrator.domain;

import org.bahmni_avni_integration.integration_data.domain.MappingGroup;

import java.util.ArrayList;
import java.util.List;

public class OpenMRSForm {
    private String uuid;
    private int formId;
    private String formName;
    private String type;
    private String program;

    private List<OpenMRSConcept> concepts = new ArrayList<>();

    public int getFormId() {
        return formId;
    }

    public void setFormId(int formId) {
        this.formId = formId;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public void addConcept(String uuid, String name) {
        OpenMRSConcept openMRSConcept = new OpenMRSConcept(uuid, name);
        concepts.add(openMRSConcept);
    }

    public List<OpenMRSConcept> getConcepts() {
        return concepts;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getProgram() {
        return program;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public MappingGroup getMappingGroup() {
        if (type.equals("Encounter")) return MappingGroup.GeneralEncounter;
        else if (type.equals("ProgramEncounter")) return MappingGroup.ProgramEncounter;
        throw new RuntimeException("Invalid form type");
    }
}
