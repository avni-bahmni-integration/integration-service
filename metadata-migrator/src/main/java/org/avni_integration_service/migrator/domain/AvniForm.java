package org.avni_integration_service.migrator.domain;

import java.util.List;

public final class AvniForm {
    private long id;
    private String name;
    private List<AvniFormElementGroup> formElementGroups;
    private AvniFormType formType;
    private String encounterType;
    private String program;
    private String subjectType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<AvniFormElementGroup> getFormElementGroups() {
        return formElementGroups;
    }

    public void setFormElementGroups(List<AvniFormElementGroup> formElementGroups) {
        this.formElementGroups = formElementGroups;
    }

    public AvniFormType getFormType() {
        return formType;
    }

    public void setFormType(AvniFormType formType) {
        this.formType = formType;
    }

    public String getEncounterType() {
        return encounterType;
    }

    public void setEncounterType(String encounterType) {
        this.encounterType = encounterType;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType;
    }

}
