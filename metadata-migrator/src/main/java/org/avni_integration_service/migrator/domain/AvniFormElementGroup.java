package org.avni_integration_service.migrator.domain;

import java.util.List;

public final class AvniFormElementGroup {
    private long id;
    private String name;
    private List<AvniFormElement> avniFormElements;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AvniFormElement> getAvniFormElements() {
        return avniFormElements;
    }

    public void setAvniFormElements(List<AvniFormElement> avniFormElements) {
        this.avniFormElements = avniFormElements;
    }
}
