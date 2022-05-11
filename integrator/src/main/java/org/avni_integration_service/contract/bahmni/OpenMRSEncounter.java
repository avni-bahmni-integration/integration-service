package org.avni_integration_service.contract.bahmni;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenMRSEncounter extends OpenMRSBaseEncounter {
    private String patient;
    private String encounterType;
    private String location;
    private String visit;

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

    public String getVisit() {
        return visit;
    }

    public void setVisit(String visit) {
        this.visit = visit;
    }
}
