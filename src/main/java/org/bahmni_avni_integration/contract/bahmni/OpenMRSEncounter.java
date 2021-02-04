package org.bahmni_avni_integration.contract.bahmni;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMRSEncounter {
    private String encounterDatetime;
    private String patient;
    private String encounterType;
    private String location;
    private List<OpenMRSEncounterProvider> encounterProviders = new ArrayList<>();
    @JsonProperty("obs")
    private List<OpenMRSSaveObservation> observations = new ArrayList<>();

    public String getEncounterDatetime() {
        return encounterDatetime;
    }

    public void setEncounterDatetime(String encounterDatetime) {
        this.encounterDatetime = encounterDatetime;
    }

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

    public List<OpenMRSEncounterProvider> getEncounterProviders() {
        return encounterProviders;
    }

    public void setEncounterProviders(List<OpenMRSEncounterProvider> encounterProviders) {
        this.encounterProviders = encounterProviders;
    }

    public List<OpenMRSSaveObservation> getObservations() {
        return observations;
    }

    public void setObservations(List<OpenMRSSaveObservation> observations) {
        this.observations = observations;
    }

    public boolean addEncounterProvider(OpenMRSEncounterProvider openMRSEncounterProvider) {
        return encounterProviders.add(openMRSEncounterProvider);
    }

    public boolean addObservation(OpenMRSSaveObservation openMRSSaveObservation) {
        return observations.add(openMRSSaveObservation);
    }
}