package org.ashwini.bahmni_avni_integration.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMRSPatient {
    private String uuid;
    private String display;
    private OpenMRSPerson person;
    private List<OpenMRSPatientIdentifier> identifiers;

    public OpenMRSPatient(OpenMRSPerson person) {
        this.person = person;
    }

    public OpenMRSPatient() {
    }

    public OpenMRSPatient addIdentifier(OpenMRSPatientIdentifier identifier) {
        if (identifiers == null) identifiers = new ArrayList<OpenMRSPatientIdentifier>();

        identifiers.add(identifier);
        return this;
    }

    public OpenMRSPerson getPerson() {
        return person;
    }

    public void setPerson(OpenMRSPerson person) {
        this.person = person;
    }

    public List<OpenMRSPatientIdentifier> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<OpenMRSPatientIdentifier> identifiers) {
        this.identifiers = identifiers;
    }

    public String getName() {
        if (getPerson().getPreferredName() != null)
            return getPerson().getPreferredName().getDisplay();
        return "";
    }

    public String getLocalName() {
        return getPerson().getLocalName();
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
}