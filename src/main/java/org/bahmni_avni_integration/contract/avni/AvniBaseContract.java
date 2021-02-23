package org.bahmni_avni_integration.contract.avni;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bahmni_avni_integration.util.FormatAndParseUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AvniBaseContract {
    protected Map<String, Object> map = new HashMap<>();

    public Object get(String name) {
        return map.get(name);
    }

    @JsonAnySetter
    public void set(final String name, final Object value) {
        map.put(name, value);
    }

    @JsonAnyGetter
    public Map<String, Object> getProperties(){
        return map;
    }

    @JsonIgnore
    public String getUuid() {
        return (String) get("ID");
    }

    @JsonIgnore
    public Date getLastModifiedDate() {
        Map<String, Object> audit = (Map<String, Object>) map.get("audit");
        String lastModifiedAtString = (String) audit.get("Last modified at");
        return FormatAndParseUtil.fromAvniDateTime(lastModifiedAtString);
    }

    @JsonIgnore
    public Object getObservation(String conceptName) {
        Map<String, Object> observations = (Map<String, Object>) get("observations");
        return observations.get(conceptName);
    }

    @Override
    public String toString() {
        return map.toString();
    }
}