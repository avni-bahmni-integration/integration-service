package org.avni_integration_service.goonj.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "Activities"
})
public class ActivityRequestDTO {

    @JsonProperty("Activities")
    private List<ActivityDTO> activities = new ArrayList<ActivityDTO>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public ActivityRequestDTO() {
    }

    /**
     * 
     * @param activities
     */
    public ActivityRequestDTO(List<ActivityDTO> activities) {
        super();
        this.activities = activities;
    }

    @JsonProperty("Activities")
    public List<ActivityDTO> getActivities() {
        return activities;
    }

    @JsonProperty("Activities")
    public void setActivities(List<ActivityDTO> activities) {
        this.activities = activities;
    }

}
