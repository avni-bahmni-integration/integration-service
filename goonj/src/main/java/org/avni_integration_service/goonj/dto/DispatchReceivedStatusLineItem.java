
package org.avni_integration_service.goonj.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "SourceId",
    "TypeOfMaterial",
    "ItemName",
    "Unit",
    "ReceivingStatus",
    "DispatchedQuantity",
    "ReceivedQuantity"
})
public class DispatchReceivedStatusLineItem {

    public static final String YES = "yes";
    @JsonProperty("SourceId")
    private String sourceId;
    @JsonProperty("TypeOfMaterial")
    private String typeOfMaterial;
    @JsonProperty("ItemName")
    private String itemName;
    @JsonProperty("Unit")
    private String unit;
    @JsonProperty("ReceivingStatus")
    private String receivingStatus;
    @JsonProperty("DispatchedQuantity")
    private long dispatchedQuantity;
    @JsonProperty("ReceivedQuantity")
    private long receivedQuantity;

    /**
     * No args constructor for use in serialization
     * 
     */
    public DispatchReceivedStatusLineItem() {
    }

    /**
     * 
     * @param sourceId
     * @param itemName
     * @param unit
     * @param typeOfMaterial
     * @param receivingStatus
     * @param receivedQuantity
     * @param dispatchedQuantity
     */
    public DispatchReceivedStatusLineItem(String sourceId, String typeOfMaterial, String itemName, String unit, String receivingStatus, long dispatchedQuantity, long receivedQuantity) {
        super();
        this.sourceId = sourceId;
        this.typeOfMaterial = typeOfMaterial;
        this.itemName = itemName;
        this.unit = unit;
        this.receivingStatus = receivingStatus;
        this.dispatchedQuantity = dispatchedQuantity;
        this.receivedQuantity = receivedQuantity;
    }

    //TODO Verify if the DispatchReceivedStatus request lineItems are created as required by Goonj SF application
    public DispatchReceivedStatusLineItem(HashMap<String, Object> entry) {
        this((String) entry.get("Dispatch Line Item Id"), (String) entry.get("Type Of Material")
                , (String) entry.get("Item Name"), (String) entry.get("Unit"),
                YES.equalsIgnoreCase((String) entry.get("Quantity matching")) ? "":"recievedPartially",
                (long) entry.get("Quantity (Dispatched)"), (long) entry.get("Quantity"));
    }

    @JsonProperty("SourceId")
    public String getSourceId() {
        return sourceId;
    }

    @JsonProperty("SourceId")
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    @JsonProperty("TypeOfMaterial")
    public String getTypeOfMaterial() {
        return typeOfMaterial;
    }

    @JsonProperty("TypeOfMaterial")
    public void setTypeOfMaterial(String typeOfMaterial) {
        this.typeOfMaterial = typeOfMaterial;
    }

    @JsonProperty("ItemName")
    public String getItemName() {
        return itemName;
    }

    @JsonProperty("ItemName")
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    @JsonProperty("Unit")
    public String getUnit() {
        return unit;
    }

    @JsonProperty("Unit")
    public void setUnit(String unit) {
        this.unit = unit;
    }

    @JsonProperty("ReceivingStatus")
    public String getReceivingStatus() {
        return receivingStatus;
    }

    @JsonProperty("ReceivingStatus")
    public void setReceivingStatus(String receivingStatus) {
        this.receivingStatus = receivingStatus;
    }

    @JsonProperty("DispatchedQuantity")
    public long getDispatchedQuantity() {
        return dispatchedQuantity;
    }

    @JsonProperty("DispatchedQuantity")
    public void setDispatchedQuantity(long dispatchedQuantity) {
        this.dispatchedQuantity = dispatchedQuantity;
    }

    @JsonProperty("ReceivedQuantity")
    public long getReceivedQuantity() {
        return receivedQuantity;
    }

    @JsonProperty("ReceivedQuantity")
    public void setReceivedQuantity(long receivedQuantity) {
        this.receivedQuantity = receivedQuantity;
    }

}
