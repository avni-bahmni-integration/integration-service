package org.avni_integration_service.goonj.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.List;
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "Demands",
        "DeletedDemands"
})
public class DemandsResponseDTO {

    @JsonProperty("Demands")
    private HashMap<String, Object>[] demands = null;
    @JsonProperty("DeletedDemands")
    private List<String> deletedDemands = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public DemandsResponseDTO() {
    }

    /**
     *
     * @param deletedDemands
     * @param demands
     */
    public DemandsResponseDTO(HashMap<String, Object>[] demands, List<String> deletedDemands) {
        super();
        this.demands = demands;
        this.deletedDemands = deletedDemands;
    }

    @JsonProperty("Demands")
    public HashMap<String, Object>[] getDemands() {
        return demands;
    }


    @JsonProperty("Demands")
    public void setDemands(HashMap<String, Object>[] demands) {
        this.demands = demands;
    }

    @JsonProperty("DeletedDemands")
    public List<String> getDeletedDemands() {
        return deletedDemands;
    }

    @JsonProperty("DeletedDemands")
    public void setDeletedDemands(List<String> deletedDemands) {
        this.deletedDemands = deletedDemands;
    }

}