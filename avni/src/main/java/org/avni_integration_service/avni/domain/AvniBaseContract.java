package org.avni_integration_service.avni.domain;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.avni_integration_service.util.FormatAndParseUtil;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AvniBaseContract implements ObservationHolder {
    private static final String ObservationsFieldName = "observations";
    protected Map<String, Object> map = new HashMap<>();

    public AvniBaseContract() {
        setObservations(new HashMap<>());
    }

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
    public String getCreatedBy() {
        Map<String, Object> audit = (Map<String, Object>) map.get("audit");
        return (String) audit.get("Created by");
    }

    @JsonIgnore
    public String getLastModifiedBy() {
        Map<String, Object> audit = (Map<String, Object>) map.get("audit");
        return (String) audit.get("Last modified by");
    }

    @JsonIgnore
    public Date getCreateDate() {
        Map<String, Object> audit = (Map<String, Object>) map.get("audit");
        String lastModifiedAtString = (String) audit.get("Created at");
        return FormatAndParseUtil.fromAvniDateTime(lastModifiedAtString);
    }

    @JsonIgnore
    public LocalDateTime getCreatedDateTime() {
        return FormatAndParseUtil.toLocalDateTime(getCreateDate());
    }

    @JsonIgnore
    public Date getLastModifiedDate() {
        Map<String, Object> audit = (Map<String, Object>) map.get("audit");
        String lastModifiedAtString = (String) audit.get("Last modified at");
        return FormatAndParseUtil.fromAvniDateTime(lastModifiedAtString);
    }

    @JsonIgnore
    public LocalDateTime getLastModifiedDateTime() {
        return FormatAndParseUtil.toLocalDateTime(getLastModifiedDate());
    }

    @JsonIgnore
    public Object getObservation(String conceptName) {
        Map<String, Object> observations = getObservations();
        return observations.get(conceptName);
    }

    @Override
    public String toString() {
        return map.toString();
    }

    @Override
    public void addObservation(String conceptName, Object value) {
        Map<String, Object> map = getObservations();
        map.put(conceptName, value);
    }

    public void setVoided(boolean voided) {
        set("Voided", voided);
    }

    @JsonIgnore
    public Boolean getVoided() {
        return (Boolean) get("Voided");
    }

    public void setObservations(Map<String, Object> observations) {
        set(ObservationsFieldName, observations);
    }

    @JsonIgnore
    public Map<String, Object> getObservations() {
        Object observations = get(ObservationsFieldName);
        if (observations == null) return new HashMap<>();
        return (Map<String, Object>) observations;
    }
}
