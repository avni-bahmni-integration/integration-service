package org.bahmni_avni_integration.contract.avni;

import org.bahmni_avni_integration.integration_data.util.FormatAndParseUtil;
import org.bahmni_avni_integration.util.Empty;
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

    @JsonIgnore
    public void setEnrolmentDateTime(Date enrolmentDateTime) {
        map.put("Enrolment date time", enrolmentDateTime);
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
        return FormatAndParseUtil.fromAvniDateTime((String) map.get("Enrolment datetime"));
    }

    @JsonIgnore
    public String getExitDateTime() {
        return (String) map.get("Exit datetime");
    }

    @JsonIgnore
    public boolean isExited() {
        return getExitDateTime() != null;
    }
}