package org.bahmni_avni_integration.contract.avni;

public class Enrolment extends AvniBaseContract {
    public String getSubjectId() {
        return (String) get("Subject ID");
    }

    public String getProgram() {
        return (String) get("Program");
    }
}