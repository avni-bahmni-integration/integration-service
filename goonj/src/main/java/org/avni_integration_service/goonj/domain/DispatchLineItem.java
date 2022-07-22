package org.avni_integration_service.goonj.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DispatchLineItem implements GoonjEntity {

    private static final List<String> Core_Fields = Arrays.asList("Unit","TypeOfMaterial","Type","Quantity",
            "PurchaseItemCategory","OtherKitDetails","MaterialName", "MaterialId", "KitId",
            "KitSubType","KitName","ItemName","ItemCategory","DispatchLineItemId","ContributedItem");

    private static final List<String> Ignored_Fields = Arrays.asList("LastUpdatedDateTime",
            "OtherKitDetails", "ItemCategory");

    private final Map<String, Object> lineItemMap;

    public DispatchLineItem(Map<String, Object> lineItemMap) {
        this.lineItemMap = lineItemMap;
    }

    @Override
    public List<String> getObservationFields() {
        return lineItemMap.keySet().stream()
                .filter(s -> Core_Fields.contains(s))
                .collect(Collectors.toList());
    }

    @Override
    public Object getValue(String responseFieldName) {
        return lineItemMap.get(responseFieldName);
    }
}
