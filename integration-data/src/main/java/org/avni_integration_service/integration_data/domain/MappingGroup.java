package org.avni_integration_service.integration_data.domain;

public class MappingGroup extends BaseEnum {
    public static final MappingGroup Common = new MappingGroup("Common", 1);

    public MappingGroup(String name, int value) {
        super(value, name);
        AllEnumTypes.add(this);
    }

    public static BaseEnum[] values() {
        return AllEnumTypes.getAllMappingGroups().toArray(BaseEnum[]::new);
    }

    public static MappingGroup valueOf(String mappingGroup) {
        return AllEnumTypes.getAllMappingGroups().stream().filter(x -> x.getName().equals(mappingGroup)).findFirst().orElse(null);
    }

    public static MappingGroup valueOf(int mappingValue) {
        return AllEnumTypes.getAllMappingGroups().stream().filter(x -> x.getValue() == mappingValue).findFirst().orElse(null);
    }
}
