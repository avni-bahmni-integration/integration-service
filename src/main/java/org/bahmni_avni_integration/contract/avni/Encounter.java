package org.bahmni_avni_integration.contract.avni;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;
import java.util.Map;

public class Encounter {
    private Map<String, Object> map = new HashMap<>();

    @JsonAnySetter
    public void set(final String name, final Object value) {
        map.put(name, value);
    }

    @JsonAnyGetter
    public Map<String, Object> getProperties(){
        return map;
    }

    public Object get(String name) {
        return map.get(name);
    }

    @Override
    public String toString() {
        return map.toString();
    }
}