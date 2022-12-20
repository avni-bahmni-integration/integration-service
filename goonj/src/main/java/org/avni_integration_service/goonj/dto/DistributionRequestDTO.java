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
    private List<DistributionDTO> distributionDTOS = new ArrayList<DistributionDTO>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public DistributionRequestDTO() {
    }

    /**
     * 
     * @param distributionDTOS
     */
    public DistributionRequestDTO(List<DistributionDTO> distributionDTOS) {
        super();
        this.distributionDTOS = distributionDTOS;
    }

    @JsonProperty("Distributions")
    public List<DistributionDTO> getDistributions() {
        return distributionDTOS;
    }

    @JsonProperty("Distributions")
    public void setDistributions(List<DistributionDTO> distributionDTOS) {
        this.distributionDTOS = distributionDTOS;
    }

}
