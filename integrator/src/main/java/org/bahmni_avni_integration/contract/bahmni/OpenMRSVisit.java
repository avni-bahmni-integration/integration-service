package org.bahmni_avni_integration.contract.bahmni;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bahmni_avni_integration.integration_data.util.FormatAndParseUtil;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMRSVisit {
    private String uuid;

    @JsonProperty("startDatetime")
    private String startDatetime;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getStartDatetime() {
        return FormatAndParseUtil.fromIsoDateString(startDatetime);
    }

    public void setStartDatetime(String startDatetime) {
        this.startDatetime = startDatetime;
    }
}