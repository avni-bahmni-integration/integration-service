package org.bahmni_avni_integration.contract.bahmni;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bahmni_avni_integration.integration_data.util.FormatAndParseUtil;

import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMRSVisit {
    private String uuid;
    private List<OpenMRSVisitAttribute> attributes;
    private OpenMRSUuidHolder visitType;

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

    public List<OpenMRSVisitAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<OpenMRSVisitAttribute> attributes) {
        this.attributes = attributes;
    }

    public OpenMRSUuidHolder getVisitType() {
        return visitType;
    }

    public void setVisitType(OpenMRSUuidHolder visitType) {
        this.visitType = visitType;
    }
}