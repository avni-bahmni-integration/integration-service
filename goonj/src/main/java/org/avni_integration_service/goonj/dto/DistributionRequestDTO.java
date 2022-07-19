package org.avni_integration_service.goonj.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "Distributions"
})
public class DistributionRequestDTO {

    @JsonProperty("Distributions")
    private List<Distribution> distributions = new ArrayList<Distribution>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public DistributionRequestDTO() {
    }

    /**
     * 
     * @param distributions
     */
    public DistributionRequestDTO(List<Distribution> distributions) {
        super();
        this.distributions = distributions;
    }

    @JsonProperty("Distributions")
    public List<Distribution> getDistributions() {
        return distributions;
    }

    @JsonProperty("Distributions")
    public void setDistributions(List<Distribution> distributions) {
        this.distributions = distributions;
    }

}
