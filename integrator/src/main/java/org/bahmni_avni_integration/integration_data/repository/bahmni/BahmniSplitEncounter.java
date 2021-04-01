package org.bahmni_avni_integration.integration_data.repository.bahmni;

import org.bahmni_avni_integration.contract.bahmni.OpenMRSObservation;

import java.util.List;

public class BahmniSplitEncounter {
    private final String formConceptSetUuid;
    private final String openMRSEncounterUuid;
    private final String openMRSEncounterDateTime;
    private final List<OpenMRSObservation> observations;
    private final boolean voided;

    public BahmniSplitEncounter(String formConceptSetUuid, String openMRSEncounterUuid, String openMRSEncounterDateTime, List<OpenMRSObservation> observations, boolean voided) {
        this.formConceptSetUuid = formConceptSetUuid;
        this.openMRSEncounterUuid = openMRSEncounterUuid;
        this.openMRSEncounterDateTime = openMRSEncounterDateTime;
        this.observations = observations;
        this.voided = voided;
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

    public boolean isVoided() {
        return voided;
    }
}