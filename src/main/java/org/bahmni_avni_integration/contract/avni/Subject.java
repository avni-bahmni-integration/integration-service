package org.bahmni_avni_integration.contract.avni;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import org.bahmni_avni_integration.util.FormatAndParseUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Subject {
    private Map<String, Object> map = new HashMap<>();
//    private static final IgnoredFields ignoredFields = new IgnoredFields("");

    @JsonAnySetter
    public void setMap(final String name, final Object value) {
        map.put(name, value);
    }

    public Object get(String name) {
        return map.get(name);
    }

    public String getUuid() {
        return (String) get("ID");
    }

    public Date getRegistrationDate() {
        String registrationDate = (String) map.get("Registration date");
        return FormatAndParseUtil.fromAvniDate(registrationDate);
    }

    public Date getLastModifiedDate() {
        Map<String, Object> audit = (Map<String, Object>) map.get("audit");
        String lastModifiedAtString = (String) audit.get("Last modified at");
        return FormatAndParseUtil.fromAvniDate(lastModifiedAtString);
    }

    public Object getObservation(String conceptName) {
        Map<String, Object> observations = (Map<String, Object>) map.get("observations");
        return observations.get(conceptName);
    }
}