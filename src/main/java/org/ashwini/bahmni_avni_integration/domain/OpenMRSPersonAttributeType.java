package org.ashwini.bahmni_avni_integration.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMRSPersonAttributeType {
    private String uuid;
    private String display;

    public OpenMRSPersonAttributeType(String uuid, String display) {
        this.uuid = uuid;
        this.display = display;
    }

    public OpenMRSPersonAttributeType() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public boolean isGivenLocalName() {
        return display.equalsIgnoreCase("givenNameLocal");
    }

    public boolean isFamilyLocalName() {
        return display.equalsIgnoreCase("familyNameLocal");
    }

    public boolean isMiddleLocalName() {
        return display.equalsIgnoreCase("middleNameLocal");
    }
}
