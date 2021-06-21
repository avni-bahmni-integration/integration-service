package org.bahmni_avni_integration.contract.bahmni;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Arrays;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenMRSSaveName {
    private String givenName;
    private String familyName;
    private boolean preferred;

    private static final String[] InvalidStringsInName = {"'", "\n", "/", "[0-9]", ","};

    public OpenMRSSaveName() {
    }

    public OpenMRSSaveName(String givenName, String familyName, boolean preferred) {
        this.givenName = givenName;
        this.familyName = familyName;
        this.preferred = preferred;
    }

    public String getGivenName() {
        return replaceInvalidString(givenName);
    }

    private String replaceInvalidString(String name) {
        if (name != null) {
            String returnName = name;
            for (String invalidString : InvalidStringsInName) {
                returnName = returnName.replaceAll(invalidString, "");
            }
            return returnName;
        }
        return null;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return replaceInvalidString(familyName);
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
