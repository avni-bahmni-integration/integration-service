package org.bahmni_avni_integration.integration_data.internal;

import java.util.HashMap;
import java.util.Map;

public class BahmniEncounterToAvniEncounterMetaData implements BahmniToAvniMetaData {
    private Map<String, String> encounterTypes = new HashMap<>();
    private String bahmniEntityUuidConcept;

    public String getAvniEncounterTypeName(String openmrsEncounterTypeUuid) {
        return encounterTypes.get(openmrsEncounterTypeUuid);
    }

    public void addEncounterType(String openmrsEncounterTypeUuid, String avniEncounterTypeName) {
        encounterTypes.put(openmrsEncounterTypeUuid, avniEncounterTypeName);
    }

    public boolean hasBahmniEncounterType(String openmrsEncounterTypeUuid) {
        return encounterTypes.containsKey(openmrsEncounterTypeUuid);
    }

    public void setBahmniEntityUuidConcept(String bahmniEntityUuidConcept) {
        this.bahmniEntityUuidConcept = bahmniEntityUuidConcept;
    }

    public String getBahmniEntityUuidConcept() {
        return bahmniEntityUuidConcept;
    }
}