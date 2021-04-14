package org.bahmni_avni_integration.contract.avni;

import org.bahmni_avni_integration.util.Empty;

import java.util.Date;

public class ProgramEncounter extends AvniBaseContract {
    public String getSubjectId() {
        return (String) get("Subject ID");
    }
    public String getSubjectType() {
        return (String) get("Subject type");
    }
    public String getProgram() {
        return (String) get("Program");
    }

    public String getEncounterType() {
        return (String) get("Encounter type");
    }

    public void setEncounterDateTime(Date date) {
        map.put("Encounter datetime", date);
    }

    public void setEncounterType(String encounterType) {
        map.put("Encounter type", encounterType);
    }

    public void setProgramEnrolment(String uuid) {
        map.put("Enrolment ID", uuid);
    }

    public void setEmptyCancelObservations() {
        map.put("cancelObservations", new Empty());
    }
}