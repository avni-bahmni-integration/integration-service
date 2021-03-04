package org.bahmni_avni_integration.contract.bahmni;

public class OpenMRSObservation {
    private String conceptUuid;
    private Object value;
    private String obsUuid;

    public String getConceptUuid() {
        return conceptUuid;
    }

    public void setConceptUuid(String uuid) {
        this.conceptUuid = uuid;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getObsUuid() {
        return obsUuid;
    }

    public void setObsUuid(String obsUuid) {
        this.obsUuid = obsUuid;
    }
}