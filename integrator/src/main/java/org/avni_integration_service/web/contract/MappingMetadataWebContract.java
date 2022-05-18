package org.avni_integration_service.web.contract;

public class MappingMetadataWebContract {
    private int id;
    private int mappingGroup;
    private int mappingType;
    private String avniValue;
    private String intSystemValue;
    private boolean coded;

    public int getMappingGroup() {
        return mappingGroup;
    }

    public void setMappingGroup(int mappingGroup) {
        this.mappingGroup = mappingGroup;
    }

    public int getMappingType() {
        return mappingType;
    }

    public void setMappingType(int mappingType) {
        this.mappingType = mappingType;
    }

    public String getAvniValue() {
        return avniValue;
    }

    public void setAvniValue(String avniValue) {
        this.avniValue = avniValue;
    }

    public String getIntSystemValue() {
        return intSystemValue;
    }

    public void setIntSystemValue(String intSystemValue) {
        this.intSystemValue = intSystemValue;
    }

    public boolean isCoded() {
        return coded;
    }

    public void setCoded(boolean coded) {
        this.coded = coded;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
