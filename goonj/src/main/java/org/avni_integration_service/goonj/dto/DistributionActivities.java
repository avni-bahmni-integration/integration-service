package org.avni_integration_service.goonj.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DistributionActivities {

    @JsonProperty("activityId")
    private String activityId;
    @JsonProperty("numberOfPersons")
    private int numberOfPersons;

    /**
     * No args constructor for use in serialization
     */
    public DistributionActivities() {
    }

    /**
     * @param activityId
     * @param numberOfPersons
     */
    public DistributionActivities(String activityId, int numberOfPersons) {
        super();
        this.activityId = activityId;
        this.numberOfPersons = numberOfPersons;
    }

    @JsonProperty("activityId")
    public String getaActivityId() {
        return activityId;
    }

    @JsonProperty("activityId")
    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    @JsonProperty("numberOfPersons")
    public int getNumberOfPersons() {
        return numberOfPersons;
    }

    @JsonProperty("numberOfPersons")
    public void setNumberOfPersons(int numberOfPersons) {
        this.numberOfPersons = numberOfPersons;
    }

}
