package org.bahmni_avni_integration.integration_data.repository.bahmni;

import org.bahmni_avni_integration.contract.bahmni.OpenMRSObservation;

import java.util.List;

public class BahmniSplitEncounter {
    private String formConceptSetUuid;
    private String openMRSEncounterUuid;
    private String openMRSEncounterDateTime;
    private List<OpenMRSObservation> observations;
    private String patientUuid;

    public BahmniSplitEncounter(String formConceptSetUuid, String openMRSEncounterUuid, String openMRSEncounterDateTime, List<OpenMRSObservation> observations, String patientUuid) {
        this.formConceptSetUuid = formConceptSetUuid;
        this.openMRSEncounterUuid = openMRSEncounterUuid;
        this.openMRSEncounterDateTime = openMRSEncounterDateTime;
        this.observations = observations;
        this.patientUuid = patientUuid;
    }

    public String getFormConceptSetUuid() {
        return formConceptSetUuid;
    }

    public String getOpenMRSEncounterUuid() {
        return openMRSEncounterUuid;
    }

    public String getOpenMRSEncounterDateTime() {
        return openMRSEncounterDateTime;
    }

    public List<OpenMRSObservation> getObservations() {
        return observations;
    }

    public String getPatientUuid() {
        return patientUuid;
    }
}