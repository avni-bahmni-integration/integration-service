package org.avni_integration_service.contract.avni;

import org.avni_integration_service.integration_data.internal.SubjectToPatientMetaData;
import org.avni_integration_service.util.AvniFormatAndParseUtil;
import org.avni_integration_service.util.FormatAndParseUtil;

import java.util.Date;

public class Subject extends AvniBaseContract {

    public String getId(SubjectToPatientMetaData subjectToPatientMetaData) {
        return (String) getObservation(subjectToPatientMetaData.avniIdentifierConcept());
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
}
