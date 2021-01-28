package org.bahmni_avni_integration.contract.bahmni;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMRSEncounter {
    private List<OpenMRSObservation> observations = new ArrayList<>();
    private String patientUuid;
    private String patientId;
    private String encounterUuid;

    public String getEncounterUuid() {
        return encounterUuid;
    }

    public String getPatientUuid() {
        return patientUuid;
    }

    public String getPatientId() {
        return patientId;
    }

    public List<OpenMRSObservation> getObservations() {
        return observations;
    }

    public void setObservations(List<OpenMRSObservation> observations) {
        this.observations = observations;
    }
}