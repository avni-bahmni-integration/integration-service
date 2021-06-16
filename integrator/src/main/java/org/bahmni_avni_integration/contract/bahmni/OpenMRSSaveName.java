package org.bahmni_avni_integration.contract.bahmni;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenMRSSaveName {
    private String givenName;
    private String familyName;
    private boolean preferred;

    public OpenMRSSaveName() {}

    public OpenMRSSaveName(String givenName, String familyName, boolean preferred) {
        this.givenName = givenName;
        this.familyName = familyName;
        this.preferred = preferred;
    }

    public String getGivenName() {
        if (givenName != null)
            return givenName.replace("'", "");
        return null;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public boolean isPreferred() {
        return preferred;
    }

    public void setPreferred(boolean preferred) {
        this.preferred = preferred;
    }
}
