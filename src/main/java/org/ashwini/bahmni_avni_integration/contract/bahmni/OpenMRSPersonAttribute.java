package org.ashwini.bahmni_avni_integration.contract.bahmni;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMRSPersonAttribute {
    private String uuid;
    private Object value;
    private OpenMRSPersonAttributeType attributeType;

    public OpenMRSPersonAttribute(String uuid, Object value, OpenMRSPersonAttributeType attributeType) {
        this.uuid = uuid;
        this.value = value;
        this.attributeType = attributeType;
    }

    public OpenMRSPersonAttribute() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public OpenMRSPersonAttributeType getAttributeType() {
        return attributeType;
    }

    public void setAttributeType(OpenMRSPersonAttributeType attributeType) {
        this.attributeType = attributeType;
    }
}
