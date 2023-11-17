package org.bahmni_avni_integration.integration_data.repository.bahmni;

import org.bahmni_avni_integration.contract.avni.Enrolment;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSObservation;
import org.joda.time.DateTime;
import org.joda.time.Minutes;

import java.util.Arrays;
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

    public Enrolment getMatchingEnrolment(Enrolment[] enrolments) {
        return Arrays.stream(enrolments).min((e1, e2) -> {
            DateTime encounterDateTime = new DateTime(this.getOpenMRSEncounterDateTime());
            DateTime enrolment1DateTime = new DateTime(e1.getEnrolmentDateTime());
            DateTime enrolment2DateTime = new DateTime(e2.getEnrolmentDateTime());
            return Long.compare(Math.abs(encounterDateTime.getMillis() - enrolment1DateTime.getMillis()), Math.abs(encounterDateTime.getMillis() - enrolment2DateTime.getMillis()));
        }).orElse(null);
    }
}
