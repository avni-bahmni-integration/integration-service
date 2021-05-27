package org.bahmni_avni_integration.web.request;

public class MappingMetadataWebRequest {
    private int id;
    private int mappingGroup;
    private int mappingType;
    private String avniValue;
    private String bahmniValue;
    private boolean isCoded;

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

    public String getBahmniValue() {
        return bahmniValue;
    }

    public void setBahmniValue(String bahmniValue) {
        this.bahmniValue = bahmniValue;
    }

    public boolean isCoded() {
        return isCoded;
    }

    public void setCoded(boolean coded) {
        isCoded = coded;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
