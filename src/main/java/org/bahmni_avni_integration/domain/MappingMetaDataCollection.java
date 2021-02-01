package org.bahmni_avni_integration.domain;

import java.util.List;

public class MappingMetaDataCollection {
    private List<MappingMetaData> list;

    public MappingMetaDataCollection(List<MappingMetaData> list) {
        this.list = list;
    }

    public String getBahmniValueForAvniValue(String avniValue) {
        MappingMetaData metaData = list.stream().filter(mappingMetaData -> avniValue.equals(mappingMetaData.getAvniValue())).findAny().orElse(null);
        if (metaData == null) return null;
        return metaData.getBahmniValue();
    }
}