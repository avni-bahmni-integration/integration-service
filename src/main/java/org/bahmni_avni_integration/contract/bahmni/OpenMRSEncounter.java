package org.bahmni_avni_integration.contract.bahmni;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMRSEncounter extends OpenMRSBaseEncounter {
    private String patient;
    private String encounterType;
    private String location;

    public String getPatient() {
        return patient;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public String getEncounterType() {
        return encounterType;
    }

    public void setEncounterType(String encounterType) {
        this.encounterType = encounterType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean addEncounterProvider(OpenMRSEncounterProvider openMRSEncounterProvider) {
        return encounterProviders.add(openMRSEncounterProvider);
    }

    public boolean addObservation(OpenMRSSaveObservation openMRSSaveObservation) {
        return observations.add(openMRSSaveObservation);
    }
}