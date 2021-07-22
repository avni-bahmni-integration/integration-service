package org.bahmni_avni_integration.contract.avni;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bahmni_avni_integration.integration_data.util.FormatAndParseUtil;
import org.bahmni_avni_integration.util.Empty;

import java.util.Date;

public class ProgramEncounter extends AvniBaseEncounter {
    @JsonIgnore
    public String getEnrolmentId() {
        return (String) get("Enrolment ID");
    }

    @JsonIgnore
    public String getSubjectType() {
        return (String) get("Subject type");
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
