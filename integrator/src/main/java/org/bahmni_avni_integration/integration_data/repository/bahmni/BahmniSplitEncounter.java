package org.bahmni_avni_integration.integration_data.repository.bahmni;

import org.bahmni_avni_integration.contract.bahmni.OpenMRSObservation;

import java.util.List;

public class BahmniSplitEncounter {
    private String formConceptSetUuid;
    private String openMRSEncounterUuid;
    private String getOpenMRSEncounterDateTime;
    private List<OpenMRSObservation> observations;

    public BahmniSplitEncounter(String formConceptSetUuid, String openMRSEncounterUuid, String getOpenMRSEncounterDateTime, List<OpenMRSObservation> observations) {
        this.formConceptSetUuid = formConceptSetUuid;
        this.openMRSEncounterUuid = openMRSEncounterUuid;
        this.getOpenMRSEncounterDateTime = getOpenMRSEncounterDateTime;
        this.observations = observations;
    }

    public String getFormConceptSetUuid() {
        return formConceptSetUuid;
    }

    public String getOpenMRSEncounterUuid() {
        return openMRSEncounterUuid;
    }

    public String getGetOpenMRSEncounterDateTime() {
        return getOpenMRSEncounterDateTime;
    }

    public List<OpenMRSObservation> getObservations() {
        return observations;
    }
}