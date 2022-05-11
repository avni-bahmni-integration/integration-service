package org.avni_integration_service.contract.bahmni;

public class OpenMRSObservation {
    private String conceptUuid;
    private Object value;
    private String obsUuid;
    private boolean voided;

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

    @Override
    public String toString() {
        return "{" +
                "conceptUuid='" + conceptUuid + '\'' +
                ", value=" + value +
                '}';
    }

    public void setVoided(boolean voided) {
        this.voided = voided;
    }

    public boolean isVoided() {
        return voided;
    }
}
