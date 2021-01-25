package org.ashwini.bahmni_avni_integration.contract.bahmni;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMRSPerson {
    private OpenMRSName preferredName;
    private String uuid;
    private String gender;
    private Date birthdate;
    private boolean birthdateEstimated;
    private OpenMRSPersonAddress preferredAddress;
    private OpenMRSPersonAttributes attributes;

    public OpenMRSPerson(OpenMRSName preferredName, String uuid, String gender, Date birthdate, boolean birthdateEstimated, OpenMRSPersonAddress preferredAddress, OpenMRSPersonAttributes attributes) {
        this.preferredName = preferredName;
        this.uuid = uuid;
        this.gender = gender;
        this.birthdate = birthdate;
        this.preferredAddress = preferredAddress;
        this.birthdateEstimated = birthdateEstimated;
        this.attributes = attributes;
    }

    public OpenMRSPerson() {
    }

    public OpenMRSPersonAddress getPreferredAddress() {
        return preferredAddress;
    }

    public void setPreferredAddress(OpenMRSPersonAddress preferredAddress) {
        this.preferredAddress = preferredAddress;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public OpenMRSName getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(OpenMRSName preferredName) {
        this.preferredName = preferredName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public boolean isBirthdateEstimated() {
        return birthdateEstimated;
    }

    public void setBirthdateEstimated(boolean birthdateEstimated) {
        this.birthdateEstimated = birthdateEstimated;
    }

    public OpenMRSPersonAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(OpenMRSPersonAttributes attributes) {
        this.attributes = attributes;
    }

    public String getLocalName() {
        return StringUtils.join(Arrays.asList(attributes.getGivenLocalName(), attributes.getMiddleLocalName(), attributes.getFamilyLocalName()), " ").trim();
    }
}