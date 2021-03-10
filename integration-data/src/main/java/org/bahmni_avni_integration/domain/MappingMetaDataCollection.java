package org.bahmni_avni_integration.domain;

import java.util.List;

public class MappingMetaDataCollection {
    private List<MappingMetaData> list;

    public MappingMetaDataCollection(List<MappingMetaData> list) {
        this.list = list;
    }

    public String getBahmniValueForAvniValue(String avniValue) {
        MappingMetaData mapping = getMappingForAvniValue(avniValue);
        if (mapping == null) return null;
        return mapping.getBahmniValue();
    }

    public String getAvniValueForBahmniValue(String bahmniValue) {
        MappingMetaData mapping = getMappingForBahmniValue(bahmniValue);
        if (mapping == null) return null;
        return mapping.getAvniValue();
    }

    public MappingMetaData getMappingForAvniValue(String avniValue) {
        return list.stream().filter(mappingMetaData -> avniValue.equals(mappingMetaData.getAvniValue())).findAny().orElse(null);
    }

    public MappingMetaData getMappingForBahmniValue(String bahmniValue) {
        return list.stream().filter(mappingMetaData -> bahmniValue.equals(mappingMetaData.getBahmniValue())).findAny().orElse(null);
    }
}