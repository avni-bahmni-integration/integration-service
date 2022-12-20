package org.avni_integration_service.migrator.domain;

import org.avni_integration_service.integration_data.domain.MappingType;

import java.util.List;
import java.util.Map;

public class StandardMappings {
    private List<Map<String, String>> list;

    public <T> StandardMappings(List<Map<String, String>> list) {
        this.list = list;
    }

    public Map<String, String> getLabMappingType(MappingType labEncounterType) {
        return getForMappingType(labEncounterType);
    }

    public Map<String, String> getForMappingType(MappingType mappingType) {
        return list.stream().filter(stringStringMap -> stringStringMap.get("MappingType").equals(mappingType.getName())).findFirst().orElse(null);
    }

    public String getAvniValueForMappingType(MappingType mappingType) {
        Map<String, String> map = this.getForMappingType(mappingType);
        return map.get("Avni Value");
    }

    public Map<String, String> getDrugOrderMappingType(MappingType bahmniMappingType) {
        return getForMappingType(bahmniMappingType);
    }

    public List<Map<String, String>> getList() {
        return list;
    }

    public Map<String, String> getDrugOrderConcept(MappingType drugOrderConcept) {
        return getForMappingType(drugOrderConcept);
    }
}
