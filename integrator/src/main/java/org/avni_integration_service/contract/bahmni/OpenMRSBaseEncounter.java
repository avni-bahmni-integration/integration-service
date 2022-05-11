package org.avni_integration_service.contract.bahmni;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class OpenMRSBaseEncounter {
    protected List<OpenMRSEncounterProvider> encounterProviders = new ArrayList<>();
    @JsonProperty("obs")
    protected List<OpenMRSSaveObservation> observations = new ArrayList<>();
    private String uuid;
    private String encounterDatetime;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getEncounterDatetime() {
        return encounterDatetime;
    }

    public void setEncounterDatetime(String encounterDatetime) {
        this.encounterDatetime = encounterDatetime;
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
}
