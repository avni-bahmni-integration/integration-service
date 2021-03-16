package org.bahmni_avni_integration.contract.avni;

import org.bahmni_avni_integration.util.Empty;
import org.bahmni_avni_integration.util.FormatAndParseUtil;

import java.util.Date;

public class Enrolment extends AvniBaseContract {
    public String getSubjectId() {
        return (String) get("Subject ID");
    }

    public String getProgram() {
        return (String) get("Program");
    }

    public void setSubjectId(String uuid) {
        map.put("Subject ID", uuid);
    }

    public void setEnrolmentDateTime(Date enrolmentDateTime) {
        map.put("Enrolment date time", enrolmentDateTime);
    }

    public void setProgram(String program) {
        map.put("Program", program);
    }

    public void setEmptyExitObservations() {
        map.put("exitObservations", new Empty());
    }

    public Date getEnrolmentDateTime() {
        return FormatAndParseUtil.fromAvniDateTime((String) map.get("Enrolment datetime"));
    }
}