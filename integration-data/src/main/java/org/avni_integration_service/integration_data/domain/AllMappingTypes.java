package org.avni_integration_service.integration_data.domain;

import java.util.ArrayList;
import java.util.List;

public class AllMappingTypes {
    private static final List<MappingType> allMappingTypes = new ArrayList<>();

    public static void add(MappingType mappingType) {
        allMappingTypes.add(mappingType);
    }

    public static List<MappingType> getAllMappingTypes() {
        return allMappingTypes;
    }
}
