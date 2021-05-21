package org.bahmni_avni_integration.contract.avni;

import org.bahmni_avni_integration.integration_data.internal.SubjectToPatientMetaData;
import org.bahmni_avni_integration.integration_data.util.FormatAndParseUtil;

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

    public String getRegistrationDate() {
        return (String) map.get("Registration date");
    }
}