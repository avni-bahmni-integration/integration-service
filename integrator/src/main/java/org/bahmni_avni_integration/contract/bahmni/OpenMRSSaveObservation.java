package org.bahmni_avni_integration.contract.bahmni;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.bahmni_avni_integration.integration_data.domain.ObsDataType;
import org.bahmni_avni_integration.integration_data.util.FormatAndParseUtil;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenMRSSaveObservation {
    private String concept;
    private String obsDatetime;
    private Object value;
    private String valueCodedName;
    private String uuid;
    private boolean voided;
    private List<OpenMRSSaveObservation> groupMembers;

    public OpenMRSSaveObservation() {
    }

    public static OpenMRSSaveObservation createVoidedObs(String uuid, String concept) {
        OpenMRSSaveObservation openMRSSaveObservation = new OpenMRSSaveObservation();
        openMRSSaveObservation.setUuid(uuid);
        openMRSSaveObservation.concept = concept;
        openMRSSaveObservation.setVoided(true);
        return openMRSSaveObservation;
    }

    public static OpenMRSSaveObservation createPrimitiveObs(String concept, Object value, ObsDataType dataType) {
        OpenMRSSaveObservation openMRSSaveObservation = new OpenMRSSaveObservation();
        openMRSSaveObservation.concept = concept;
        openMRSSaveObservation.value = getValue(value, dataType);
        return openMRSSaveObservation;
    }

    public static OpenMRSSaveObservation createPrimitiveObs(String obsUuid, String concept, Object value, ObsDataType dataType) {
        OpenMRSSaveObservation openMRSSaveObservation = new OpenMRSSaveObservation();
        openMRSSaveObservation.uuid = obsUuid;
        openMRSSaveObservation.concept = concept;
        openMRSSaveObservation.value = getValue(value, dataType);
        return openMRSSaveObservation;
    }

    private static Object getValue(Object value, ObsDataType dataType) {
        return ObsDataType.Date.equals(dataType)
                ? FormatAndParseUtil.fromAvniToOpenMRSDate(value.toString())
                : value;
    }

    public static OpenMRSSaveObservation createCodedObs(String concept, String valueUuid) {
        OpenMRSSaveObservation openMRSSaveObservation = new OpenMRSSaveObservation();
        openMRSSaveObservation.concept = concept;
        openMRSSaveObservation.value = valueUuid;
        return openMRSSaveObservation;
    }

    public static OpenMRSSaveObservation createCodedObs(String obsUuid, String concept, String valueUuid) {
        OpenMRSSaveObservation openMRSSaveObservation = new OpenMRSSaveObservation();
        openMRSSaveObservation.uuid = obsUuid;
        openMRSSaveObservation.concept = concept;
        openMRSSaveObservation.value = valueUuid;
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

    public boolean isVoided() {
        return voided;
    }

    public void setVoided(boolean voided) {
        this.voided = voided;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<OpenMRSSaveObservation> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(List<OpenMRSSaveObservation> groupMembers) {
        this.groupMembers = groupMembers;
    }
}