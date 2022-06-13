package org.avni_integration_service.avni.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.avni_integration_service.util.FormatAndParseUtil;
import org.avni_integration_service.util.MapUtil;

import java.util.Date;

public class Subject extends AvniBaseContract {
    public static final String SubjectTypeFieldName = "Subject type";
    public static final String AddressFieldName = "Address";
    public static final String ExternalIdFieldName = "External ID";
    public static final String FirstNameFieldName = "First name";
    public static final String RegistrationDateFieldName = "Registration date";

    public String getId(String avniIdentifierConcept) {
        return (String) getObservation(avniIdentifierConcept);
    }

    @JsonIgnore
    public String getFirstName() {
        return (String) getObservation(FirstNameFieldName);
    }

    public void setFirstName(String firstName) {
        set(FirstNameFieldName, firstName);
    }

    @JsonIgnore
    public String getLastName() {
        return (String) getObservation("Last name");
    }

    @JsonIgnore
    public String getDateOfBirth() {
        return (String) getObservation("Date of birth");
    }

    public void setRegistrationDate(Date date) {
        set(RegistrationDateFieldName, FormatAndParseUtil.toISODateString(date));
    }

    @JsonIgnore
    public Date getRegistrationDate() {
        var registrationDate = (String) map.get(RegistrationDateFieldName);
        return registrationDate == null ? null : FormatAndParseUtil.fromAvniDate(registrationDate);
    }

    public void setSubjectType(String subjectTYpe) {
        this.set(Subject.SubjectTypeFieldName, subjectTYpe);
    }

    public void setExternalId(String externalId) {
        map.put(ExternalIdFieldName, externalId);
    }

    @JsonIgnore
    public String getExternalId() {
        return MapUtil.getString(ExternalIdFieldName, this.map);
    }

    public void setAddress(String address) {
        map.put(AddressFieldName, address);
    }

    @JsonIgnore
    public String getAddress() {
        return MapUtil.getString(AddressFieldName, this.map);
    }
}
