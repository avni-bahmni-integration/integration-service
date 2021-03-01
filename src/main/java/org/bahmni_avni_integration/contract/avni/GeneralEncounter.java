package org.bahmni_avni_integration.contract.avni;

import java.util.Date;

public class GeneralEncounter extends AvniBaseContract {
    public void setEncounterDateTime(Date date) {
        map.put("Encounter datetime", date);
    }

    public void setEncounterType(String encounterType) {
        map.put("Encounter type", encounterType);
    }

    public void setSubjectId(String uuid) {
        map.put("Subject ID", uuid);
    }

    public String getSubjectExternalId() {
        return (String) map.get("Subject ID");
    }
}