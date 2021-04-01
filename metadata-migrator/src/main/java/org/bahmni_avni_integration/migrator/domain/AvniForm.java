package org.bahmni_avni_integration.migrator.domain;

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

    public String getEncounterType() {
        return switch (formType) {
            case IndividualProfile -> subjectType;
            case ProgramEncounter, Encounter -> encounterType;
            case ProgramEnrolment -> program;
        };
    }
}