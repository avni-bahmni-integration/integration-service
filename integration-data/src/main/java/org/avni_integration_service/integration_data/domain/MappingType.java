package org.avni_integration_service.integration_data.domain;

//Todo: after another integration move the ones not generic to Bahmni module
public class MappingType extends BaseEnum {
    public static MappingType Concept = new MappingType(2, "Concept");

    public MappingType(int value, String name) {
        super(value, name);
        AllEnumTypes.add(this);
    }

    public static BaseEnum[] values() {
        return AllEnumTypes.getAllMappingTypes().toArray(BaseEnum[]::new);
    }

    public static MappingType valueOf(String mappingType) {
        return AllEnumTypes.getAllMappingTypes().stream().filter(x -> x.getName().equals(mappingType)).findFirst().orElse(null);
    }

    public static MappingType valueOf(int mappingValue) {
        return AllEnumTypes.getAllMappingTypes().stream().filter(x -> x.getValue() == mappingValue).findFirst().orElse(null);
    }
}
