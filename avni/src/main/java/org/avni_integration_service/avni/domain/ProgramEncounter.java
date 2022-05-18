package org.avni_integration_service.avni.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ProgramEncounter extends AvniBaseEncounter {
    @JsonIgnore
    public String getEnrolmentId() {
        return (String) get("Enrolment ID");
    }

    @JsonIgnore
    public String getSubjectType() {
        return (String) get(Subject.SubjectTypeFieldName);
    }

    @JsonIgnore
    public String getProgram() {
        return (String) get("Program");
    }

    @JsonIgnore
    public String getEarliestScheduledDate() {
        return (String) get("Earliest scheduled date");
    }

    public void setProgramEnrolment(String uuid) {
        map.put("Enrolment ID", uuid);
    }
}
