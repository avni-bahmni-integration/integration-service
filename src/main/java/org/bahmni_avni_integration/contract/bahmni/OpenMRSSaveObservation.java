package org.bahmni_avni_integration.contract.bahmni;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMRSSaveObservation {
    private String concept;
    private Date obsDatetime;
    private String value;
    private String valueCodedName;

    public OpenMRSSaveObservation() {
    }

    public OpenMRSSaveObservation(String concept, Date obsDatetime, String value, String valueCodedName) {
        this.concept = concept;
        this.obsDatetime = obsDatetime;
        this.value = value;
        this.valueCodedName = valueCodedName;
    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public Date getObsDatetime() {
        return obsDatetime;
    }

    public void setObsDatetime(Date obsDatetime) {
        this.obsDatetime = obsDatetime;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValueCodedName() {
        return valueCodedName;
    }

    public void setValueCodedName(String valueCodedName) {
        this.valueCodedName = valueCodedName;
    }
}