package org.avni_integration_service.contract.avni;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.avni_integration_service.util.AvniFormatAndParseUtil;
import org.avni_integration_service.util.FormatAndParseUtil;

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
    public void setUuid(String uuid) {
        set("ID", uuid);
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

    public void addObservation(String conceptName, Object value) {
        Object observations = get("observations");
        if (observations == null) set("observations", new HashMap<String, Object>());

        Map<String, Object> map = (Map<String, Object>) get("observations");
        map.put(conceptName, value);
    }

    public void setVoided(boolean voided) {
        set("Voided", voided);
    }

    public Boolean getVoided() {
        return (Boolean) get("Voided");
    }
}
