package org.bahmni_avni_integration.contract.bahmni;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.bahmni_avni_integration.domain.ObsDataType;
import org.bahmni_avni_integration.util.FormatAndParseUtil;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenMRSSaveObservation {
    private String concept;
    private String obsDatetime;
    private Object value;
    private String valueCodedName;

    public OpenMRSSaveObservation() {
    }

    public static OpenMRSSaveObservation createPrimitiveObs(String concept, Object value, ObsDataType dataType) {
        OpenMRSSaveObservation openMRSSaveObservation = new OpenMRSSaveObservation();
        openMRSSaveObservation.concept = concept;
        openMRSSaveObservation.value = getValue(value, dataType);
        return openMRSSaveObservation;
    }

    private static Object getValue(Object value, ObsDataType dataType) {
        if (ObsDataType.Date.equals(dataType))
            return FormatAndParseUtil.fromAvniToOpenMRSDate(value.toString());
        return value;
    }

    public static OpenMRSSaveObservation createCodedObs(String concept, String valueCodedName) {
        OpenMRSSaveObservation openMRSSaveObservation = new OpenMRSSaveObservation();
        openMRSSaveObservation.concept = concept;
        openMRSSaveObservation.valueCodedName = valueCodedName;
        return openMRSSaveObservation;
    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public String getObsDatetime() {
        return obsDatetime;
    }

    public void setObsDatetime(String obsDatetime) {
        this.obsDatetime = obsDatetime;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getValueCodedName() {
        return valueCodedName;
    }

    public void setValueCodedName(String valueCodedName) {
        this.valueCodedName = valueCodedName;
    }
}