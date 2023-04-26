package org.avni_integration_service.goonj.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DistributionActivities {

    @JsonProperty("ActivitySourceId")
    private String ActivitySourceId;
    @JsonProperty("numberOfPersons")
    private int numberOfPersons;

    /**
     * No args constructor for use in serialization
     */
    public DistributionActivities() {
    }

    /**
     * @param ActivitySourceId
     * @param numberOfPersons
     */
    public DistributionActivities(String ActivitySourceId, int numberOfPersons) {
        super();
        this.ActivitySourceId = ActivitySourceId;
        this.numberOfPersons = numberOfPersons;
    }

    @JsonProperty("ActivitySourceId")
    public String getaActivityId() {
        return ActivitySourceId;
    }

    @JsonProperty("ActivitySourceId")
    public void setActivityId(String activitySourceId) {
        this.ActivitySourceId = activitySourceId;
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
