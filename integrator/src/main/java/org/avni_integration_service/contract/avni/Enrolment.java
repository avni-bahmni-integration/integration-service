package org.avni_integration_service.contract.avni;

import org.avni_integration_service.integration_data.util.FormatAndParseUtil;
import org.avni_integration_service.util.Empty;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

public class Enrolment extends AvniBaseContract {
    @JsonIgnore
    public String getSubjectId() {
        return (String) get("Subject ID");
    }

    @JsonIgnore
    public String getProgram() {
        return (String) get("Program");
    }

    public void setSubjectId(String uuid) {
        map.put("Subject ID", uuid);
    }

    //TODO: Use separate contracts for read and write.
    // Why? When using for read, enrolmentDateTime is a string but for write we are adding a date object.
    // This makes the code confusing to others who read it later
    @JsonIgnore
    public void setEnrolmentDateTime(Date enrolmentDateTime) {
        map.put("Enrolment datetime", enrolmentDateTime);
    }

    @JsonIgnore
    public void setProgram(String program) {
        map.put("Program", program);
    }

    public void setEmptyExitObservations() {
        map.put("exitObservations", new Empty());
    }

    @JsonIgnore
    public Date getEnrolmentDateTime() {
        var enrolmentDateTime = (String) map.get("Enrolment datetime");
        return enrolmentDateTime == null ? null : FormatAndParseUtil.fromAvniDateTime(enrolmentDateTime);
    }

    @JsonIgnore
    public Date getExitDateTime() {
        var exitDateTime = (String) map.get("Exit datetime");
        return exitDateTime == null ? null : FormatAndParseUtil.fromAvniDateTime(exitDateTime);
    }

    @JsonIgnore
    public boolean isExited() {
        return getExitDateTime() != null;
    }
}
