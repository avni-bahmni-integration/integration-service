package org.bahmni_avni_integration.migrator.domain;

import org.bahmni_avni_integration.integration_data.domain.MappingGroup;
import org.bahmni_avni_integration.integration_data.domain.MappingType;

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

    public String getAvniValueForMapping() {
        return switch (formType) {
            case IndividualProfile -> null;
            case ProgramEncounter, Encounter -> encounterType;
            case ProgramEnrolment -> program;
        };
    }

    public MappingType getMappingType() {
        return switch (formType) {
            case IndividualProfile -> MappingType.CommunityRegistration_BahmniForm;
            case Encounter -> MappingType.CommunityEncounter_BahmniForm;
            case ProgramEncounter -> MappingType.CommunityProgramEncounter_BahmniForm;
            case ProgramEnrolment -> MappingType.CommunityEnrolment_BahmniForm;
        };
    }

    public MappingGroup getMappingGroup() {
        return switch (formType) {
            case IndividualProfile -> MappingGroup.PatientSubject;
            case Encounter -> MappingGroup.GeneralEncounter;
            case ProgramEncounter -> MappingGroup.ProgramEncounter;
            case ProgramEnrolment -> MappingGroup.ProgramEnrolment;
        };
    }
}