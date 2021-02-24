package org.bahmni_avni_integration.contract.internal;

import java.util.HashMap;
import java.util.Map;

public class BahmniEncounterToAvniEncounterMetaData implements BahmniToAvniMetaData {
    private Map<String, String> encounterTypes = new HashMap<>();
    private String patientUuidConcept;

    public String getAvniEncounterTypeName(String openmrsEncounterTypeUuid) {
        return encounterTypes.get(openmrsEncounterTypeUuid);
    }

    public void addEncounterType(String openmrsEncounterTypeUuid, String avniEncounterTypeName) {
        encounterTypes.put(openmrsEncounterTypeUuid, avniEncounterTypeName);
    }

    public boolean hasBahmniEncounterType(String openmrsEncounterTypeUuid) {
        return encounterTypes.containsKey(openmrsEncounterTypeUuid);
    }

    public void setPatientUuidConcept(String patientUuidConcept) {
        this.patientUuidConcept = patientUuidConcept;
    }

    public String getPatientUuidConcept() {
        return patientUuidConcept;
    }
}