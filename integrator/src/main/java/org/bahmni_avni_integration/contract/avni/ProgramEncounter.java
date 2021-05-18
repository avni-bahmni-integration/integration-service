package org.bahmni_avni_integration.contract.avni;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bahmni_avni_integration.integration_data.util.FormatAndParseUtil;
import org.bahmni_avni_integration.util.Empty;

import java.util.Date;

public class ProgramEncounter extends AvniBaseContract {

    @JsonIgnore
    public String getSubjectId() {
        return (String) get("Subject ID");
    }

    @JsonIgnore
    public String getSubjectType() {
        return (String) get("Subject type");
    }

    @JsonIgnore
    public String getProgram() {
        return (String) get("Program");
    }

    @JsonIgnore
    public String getEncounterType() {
        return (String) get("Encounter type");
    }

    @JsonIgnore
    public Date getEncounterDateTime() {
        var encounterDateTime = (String) map.get("Encounter date time");
        return encounterDateTime == null ? null : FormatAndParseUtil.fromAvniDateTime(encounterDateTime);
    }

    @JsonIgnore
    public String getEarliestScheduledDate() {
        return (String) get("Earliest scheduled date");
    }

    @JsonIgnore
    public boolean isCompleted() {
        return getEncounterDateTime() != null;
    }

    public void setEncounterDateTime(Date date) {
        map.put("Encounter date time", date);
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