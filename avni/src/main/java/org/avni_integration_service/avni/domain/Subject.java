package org.avni_integration_service.avni.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.avni_integration_service.util.FormatAndParseUtil;
import org.avni_integration_service.util.MapUtil;

import java.util.Date;

public class Subject extends AvniBaseContract {
    public static final String SubjectTypeFieldName = "Subject type";
    public static final String AddressFieldName = "Address";
    public static final String ExternalIdFieldName = "External ID";

    public String getId(String avniIdentifierConcept) {
        return (String) getObservation(avniIdentifierConcept);
    }

    public String getFirstName() {
        return (String) getObservation("First name");
    }

    public String getLastName() {
        return (String) getObservation("Last name");
    }

    public String getDateOfBirth() {
        return (String) getObservation("Date of birth");
    }

    public Date getRegistrationDate() {
        var registrationDate = (String) map.get("Registration date");
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
