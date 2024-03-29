package org.bahmni_avni_integration.contract.avni;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bahmni_avni_integration.integration_data.util.FormatAndParseUtil;
import org.bahmni_avni_integration.util.Empty;

import java.util.Date;

public abstract class AvniBaseEncounter extends AvniBaseContract {
    public void setEncounterDateTime(Date date) {
        map.put("Encounter date time", date);
    }

    public void setEncounterDateTime(String date) {
        map.put("Encounter date time", date);
    }

    public void setEncounterType(String encounterType) {
        map.put("Encounter type", encounterType);
    }

    public void setSubjectId(String uuid) {
        map.put("Subject ID", uuid);
    }

    @JsonIgnore
    public String getSubjectId() {
        return (String) map.get("Subject ID");
    }

    @JsonIgnore
    public Date getEncounterDateTime() {
        var encounterDateTime = (String) map.get("Encounter date time");
        return encounterDateTime == null ? null : FormatAndParseUtil.fromAvniDateTime(encounterDateTime);
    }

    @JsonIgnore
    public boolean isCompleted() {
        return getEncounterDateTime() != null;
    }

    public void setEmptyCancelObservations() {
        map.put("cancelObservations", new Empty());
    }

    @JsonIgnore
    public String getEncounterType() {
        return (String) get("Encounter type");
    }
}
