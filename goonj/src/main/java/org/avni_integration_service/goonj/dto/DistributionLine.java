
package org.avni_integration_service.goonj.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
    "sourceId",
    "distributedTo",
    "implementationInventoryId",
    "numberOfdistributions",
    "quantity",
    "unit"
})
public class DistributionLine {

    @JsonProperty("sourceId")
    private String sourceId;
    @JsonProperty("distributedTo")
    private String distributedTo;
    @JsonProperty("implementationInventoryId")
    private String implementationInventoryId;
    @JsonProperty("numberOfdistributions")
    private int numberofdistributions;
    @JsonProperty("quantity")
    private int quantity;
    @JsonProperty("unit")
    private String unit;

    /**
     * No args constructor for use in serialization
     * 
     */
    public DistributionLine() {
    }

    /**
     * 
     * @param sourceId
     * @param distributedTo
     * @param quantity
     * @param unit
     * @param implementationInventoryId
     * @param numberofdistributions
     */
    public DistributionLine(String sourceId, String distributedTo, String implementationInventoryId, int numberofdistributions, int quantity, String unit) {
        super();
        this.sourceId = sourceId;
        this.distributedTo = distributedTo;
        this.implementationInventoryId = implementationInventoryId;
        this.numberofdistributions = numberofdistributions;
        this.quantity = quantity;
        this.unit = unit;
    }

    @JsonProperty("sourceId")
    public String getSourceId() {
        return sourceId;
    }
    @JsonProperty("sourceId")
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }
    @JsonProperty("distributedTo")
    public String getDistributedTo() {
        return distributedTo;
    }
    @JsonProperty("distributedTo")
    public void setDistributedTo(String distributedTo) {
        this.distributedTo = distributedTo;
    }
    @JsonProperty("implementationInventoryId")
    public String getimplementationInventoryId() {
        return implementationInventoryId;
    }
    @JsonProperty("implementationInventoryId")
    public void setimplementationInventoryId(String implementationInventoryId) {
        this.implementationInventoryId = implementationInventoryId;
    }
    @JsonProperty("numberOfdistributions")
    public int getNumberofdistributions() {
        return numberofdistributions;
    }
    @JsonProperty("numberOfdistributions")
    public void setNumberofdistributions(int numberofdistributions) {
        this.numberofdistributions = numberofdistributions;
    }
    @JsonProperty("quantity")
    public int getQuantity() {
        return quantity;
    }
    @JsonProperty("quantity")
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    @JsonProperty("unit")
    public String getUnit() {
        return unit;
    }
    @JsonProperty("unit")
    public void setUnit(String unit) {
        this.unit = unit;
    }

}

