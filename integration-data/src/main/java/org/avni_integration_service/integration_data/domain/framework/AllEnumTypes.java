package org.avni_integration_service.integration_data.domain.framework;

import org.avni_integration_service.integration_data.domain.MappingGroup;
import org.avni_integration_service.integration_data.domain.MappingType;

import java.util.ArrayList;
import java.util.List;

public class AllEnumTypes {
    private static final List<MappingType> allMappingTypes = new ArrayList<>();
    private static final List<MappingGroup> allMappingGroups = new ArrayList<>();

    public static void add(MappingType mappingType) {
        allMappingTypes.add(mappingType);
    }

    public static List<MappingType> getAllMappingTypes() {
        return allMappingTypes;
    }

    public static void add(MappingGroup mappingGroup) {
        allMappingGroups.add(mappingGroup);
    }

    public static List<MappingGroup> getAllMappingGroups() {
        return allMappingGroups;
    }
}
