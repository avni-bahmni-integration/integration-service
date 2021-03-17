package org.bahmni_avni_integration.migrator.domain;

import java.util.ArrayList;
import java.util.List;

public final class AvniForm {
    private long id;
    private String name;
    private List<AvniFormElementGroup> formElementGroups;

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
}