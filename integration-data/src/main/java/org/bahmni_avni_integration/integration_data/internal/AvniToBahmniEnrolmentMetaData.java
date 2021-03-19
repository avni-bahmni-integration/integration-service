package org.bahmni_avni_integration.integration_data.internal;

public class AvniToBahmniEnrolmentMetaData implements AvniToBahmniMetaData {
    private String avniEntityUuidConcept;

    public void setAvniEntityUuidConcept(String avniEntityUuidConcept) {
        this.avniEntityUuidConcept = avniEntityUuidConcept;
    }

    @Override
    public String getAvniEntityUuidConcept() {
        return avniEntityUuidConcept;
    }
}