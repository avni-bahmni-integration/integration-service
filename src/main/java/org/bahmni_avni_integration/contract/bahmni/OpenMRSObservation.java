package org.bahmni_avni_integration.contract.bahmni;

public class OpenMRSObservation {
    private String uuid;
    private Object value;

    public void setConcept(String uuid) {
        this.uuid = uuid;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getUuid() {
        return uuid;
    }

    public Object getValue() {
        return value;
    }
}