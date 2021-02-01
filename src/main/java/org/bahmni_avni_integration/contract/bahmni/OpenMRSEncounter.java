package org.bahmni_avni_integration.contract.bahmni;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;
import java.util.Map;

public class OpenMRSEncounter {
    private Map<String, Object> map = new HashMap<>();

    @JsonAnySetter
    public void setMap(final String name, final Object value) {
        map.put(name, value);
    }

    public Object get(String name) {
        return map.get(name);
    }
}