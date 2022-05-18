package org.avni_integration_service.avni.domain;

import org.avni_integration_service.util.FormatAndParseUtil;

import java.util.Date;

public class Subject extends AvniBaseContract {
    public static final String SubjectTypeFieldName = "Subject type";
    public static final String AddressFieldName = "Address";

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
}
