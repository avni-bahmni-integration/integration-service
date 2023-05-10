
package org.avni_integration_service.goonj.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
    "SourceId",
    "TypeOfMaterial",
    "ItemName",
    "DispatchStatusLineItem",
    "Unit",
    "ReceivingStatus",
    "DispatchedQuantity",
    "ReceivedQuantity"
})
public class DispatchReceivedStatusLineItem {
    @JsonProperty("SourceId")
    private String sourceId;
    @JsonProperty("TypeOfMaterial")
    private String typeOfMaterial;
    @JsonProperty("ItemName")
    private String itemName;
    @JsonProperty("DispatchStatusLineItem")
    private String dispatchStatusLineItem;
    @JsonProperty("Unit")
    private String unit;
    @JsonProperty("ReceivingStatus")
    private String receivingStatus;
    @JsonProperty("DispatchedQuantity")
    private int dispatchedQuantity;
    @JsonProperty("ReceivedQuantity")
    private int receivedQuantity;

    /**
     * No args constructor for use in serialization
     * 
     */
    public DispatchReceivedStatusLineItem() {
    }

    /**
     * @param sourceId
     * @param typeOfMaterial
     * @param itemName
     * @param dispatchStatusLineItem
     * @param unit
     * @param receivingStatus
     * @param dispatchedQuantity
     * @param receivedQuantity
     */
    public DispatchReceivedStatusLineItem(String sourceId, String typeOfMaterial, String itemName, String dispatchStatusLineItem, String unit, String receivingStatus, int dispatchedQuantity, int receivedQuantity) {
        super();
        this.sourceId = sourceId;
        this.typeOfMaterial = typeOfMaterial;
        this.itemName = itemName;
        this.dispatchStatusLineItem = dispatchStatusLineItem;
        this.unit = unit;
        this.receivingStatus = receivingStatus;
        this.dispatchedQuantity = dispatchedQuantity;
        this.receivedQuantity = receivedQuantity;
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

    @JsonProperty("DispatchStatusLineItem")
    public String getDispatchStatusLineItem() {
        return dispatchStatusLineItem;
    }

    @JsonProperty("DispatchStatusLineItem")
    public void setDispatchStatusLineItem(String dispatchStatusLineItem) {
        this.dispatchStatusLineItem = dispatchStatusLineItem;
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
    public int getDispatchedQuantity() {
        return dispatchedQuantity;
    }

    @JsonProperty("DispatchedQuantity")
    public void setDispatchedQuantity(int dispatchedQuantity) {
        this.dispatchedQuantity = dispatchedQuantity;
    }

    @JsonProperty("ReceivedQuantity")
    public int getReceivedQuantity() {
        return receivedQuantity;
    }

    @JsonProperty("ReceivedQuantity")
    public void setReceivedQuantity(int receivedQuantity) {
        this.receivedQuantity = receivedQuantity;
    }

}
