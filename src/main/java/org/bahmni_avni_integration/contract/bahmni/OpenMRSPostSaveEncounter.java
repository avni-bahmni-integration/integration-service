package org.bahmni_avni_integration.contract.bahmni;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMRSPostSaveEncounter extends OpenMRSBaseEncounter {
    private OpenMRSUuidHolder patient;
    private OpenMRSUuidHolder encounterType;
    private OpenMRSUuidHolder location;

    public OpenMRSUuidHolder getPatient() {
        return patient;
    }

    public void setPatient(OpenMRSUuidHolder patient) {
        this.patient = patient;
    }

    public OpenMRSUuidHolder getEncounterType() {
        return encounterType;
    }

    public void setEncounterType(OpenMRSUuidHolder encounterType) {
        this.encounterType = encounterType;
    }

    public OpenMRSUuidHolder getLocation() {
        return location;
    }

    public void setLocation(OpenMRSUuidHolder location) {
        this.location = location;
    }
}