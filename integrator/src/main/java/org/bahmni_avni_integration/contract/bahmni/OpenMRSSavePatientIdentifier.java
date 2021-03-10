package org.bahmni_avni_integration.contract.bahmni;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenMRSSavePatientIdentifier {
    private String identifier;
    private String identifierType;
    private String location;
    private boolean preferred;

    public OpenMRSSavePatientIdentifier() {
    }

    public OpenMRSSavePatientIdentifier(String identifier, String identifierType, String location, boolean preferred) {
        this.identifier = identifier;
        this.identifierType = identifierType;
        this.location = location;
        this.preferred = preferred;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public boolean isPreferred() {
        return preferred;
    }

    public void setPreferred(boolean preferred) {
        this.preferred = preferred;
    }

    public String getIdentifierType() {
        return identifierType;
    }

    public void setIdentifierType(String identifierType) {
        this.identifierType = identifierType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}