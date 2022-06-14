package org.avni_integration_service.goonj.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DispatchLineItem implements GoonjEntity {
    private static final List<String> Ignored_Fields = Arrays.asList("OtherKitDetails", "ItemCategory");

    private final Map<String, Object> lineItemMap;

    public DispatchLineItem(Map<String, Object> lineItemMap) {
        this.lineItemMap = lineItemMap;
    }

    @Override
    public List<String> getObservationFields() {
        return lineItemMap.keySet().stream().toList();
    }

    @Override
    public Object getValue(String responseFieldName) {
        return lineItemMap.get(responseFieldName);
    }
}
