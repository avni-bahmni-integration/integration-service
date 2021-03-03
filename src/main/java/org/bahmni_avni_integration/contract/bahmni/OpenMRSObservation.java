package org.bahmni_avni_integration.contract.bahmni;

public class OpenMRSObservation {
    private String uuid;
    private Object value;
    private String obsUuid;

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

    public String getObsUuid() {
        return obsUuid;
    }

    public void setObsUuid(String obsUuid) {
        this.obsUuid = obsUuid;
    }
}