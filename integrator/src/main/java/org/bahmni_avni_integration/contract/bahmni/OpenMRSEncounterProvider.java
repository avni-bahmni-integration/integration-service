package org.bahmni_avni_integration.contract.bahmni;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMRSEncounterProvider {
    private String uuid;
    private String provider;
    private String encounterRole;

    public OpenMRSEncounterProvider() {
    }

    public OpenMRSEncounterProvider(String provider, String encounterRole) {
        this.provider = provider;
        this.encounterRole = encounterRole;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getEncounterRole() {
        return encounterRole;
    }

    public void setEncounterRole(String encounterRole) {
        this.encounterRole = encounterRole;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}