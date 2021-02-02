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

    public static OpenMRSSaveObservation createPrimitiveObs(String concept, Date obsDatetime, String value) {
        OpenMRSSaveObservation openMRSSaveObservation = new OpenMRSSaveObservation();
        openMRSSaveObservation.concept = concept;
        openMRSSaveObservation.obsDatetime = obsDatetime;
        openMRSSaveObservation.value = value;
        return openMRSSaveObservation;
    }

    public static OpenMRSSaveObservation createCodedObs(String concept, Date obsDatetime, String valueCodedName) {
        OpenMRSSaveObservation openMRSSaveObservation = new OpenMRSSaveObservation();
        openMRSSaveObservation.concept = concept;
        openMRSSaveObservation.obsDatetime = obsDatetime;
        openMRSSaveObservation.valueCodedName = valueCodedName;
        return openMRSSaveObservation;
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