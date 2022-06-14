package org.avni_integration_service.avni.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GeneralEncounter extends AvniBaseEncounter {
    @JsonProperty("External ID")
    private String externalID;
    @JsonProperty("Subject external ID")
    private String subjectExternalID;

    public String getExternalID() {
        return externalID;
    }

    public void setExternalID(String externalID) {
        this.externalID = externalID;
    }

    public String getSubjectExternalID() {
        return subjectExternalID;
    }

    public void setSubjectExternalID(String subjectExternalID) {
        this.subjectExternalID = subjectExternalID;
    }
}
