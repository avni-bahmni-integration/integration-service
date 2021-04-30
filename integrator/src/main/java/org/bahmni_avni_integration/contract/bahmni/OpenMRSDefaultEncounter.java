package org.bahmni_avni_integration.contract.bahmni;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMRSDefaultEncounter {
    private final Map<String, Object> map = new HashMap<>();

    @JsonAnySetter
    public void setAny(String name, Object obj) {
        map.put(name, obj);
    }

    public boolean isNotVoided(String uuid) {
        List<Map<String, Object>> orders = (List<Map<String, Object>>) map.get("orders");
        Map<String, Object> order = orders.stream().filter(stringObjectMap -> stringObjectMap.get("uuid").equals(uuid)).findFirst().orElse(null);
        if (order == null)
            throw new RuntimeException("Something very wrong here, order found in full encounter but not in default encounter");

        return order.get("voided") == null || order.get("voided").equals(false);
    }
}