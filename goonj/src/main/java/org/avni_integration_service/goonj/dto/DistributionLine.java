
package org.avni_integration_service.goonj.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
    "SourceId",
    "RecordType",
    "ContributedItem",
    "DistributedTo",
    "Eachlineitemquantity",
    "Kit",
    "Kitlineitem",
    "KitQuantity",
    "MaterialInventory",
    "MaterialName",
    "Numberofdistributions",
    "Quantity",
    "Unit"
})
public class DistributionLine {

    @JsonProperty("SourceId")
    private String sourceId;
    @JsonProperty("RecordType")
    private String recordType;
    @JsonProperty("ContributedItem")
    private String contributedItem;
    @JsonProperty("DistributedTo")
    private String distributedTo;
    @JsonProperty("Eachlineitemquantity")
    private Long eachlineitemquantity;
    @JsonProperty("Kit")
    private String kit;
    @JsonProperty("Kitlineitem")
    private String kitlineitem;
    @JsonProperty("KitQuantity")
    private Long kitQuantity;
    @JsonProperty("MaterialInventory")
    private String materialInventory;
    @JsonProperty("MaterialName")
    private String materialName;
    @JsonProperty("Numberofdistributions")
    private Long numberofdistributions;
    @JsonProperty("Quantity")
    private Long quantity;
    @JsonProperty("Unit")
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
     * @param recordType
     * @param kitlineitem
     * @param eachlineitemquantity
     * @param materialName
     * @param unit
     * @param materialInventory
     * @param kit
     * @param contributedItem
     * @param kitQuantity
     * @param numberofdistributions
     */
    public DistributionLine(String sourceId, String recordType, String contributedItem, String distributedTo, Long eachlineitemquantity, String kit, String kitlineitem, Long kitQuantity, String materialInventory, String materialName, Long numberofdistributions, Long quantity, String unit) {
        super();
        this.sourceId = sourceId;
        this.recordType = recordType;
        this.contributedItem = contributedItem;
        this.distributedTo = distributedTo;
        this.eachlineitemquantity = eachlineitemquantity;
        this.kit = kit;
        this.kitlineitem = kitlineitem;
        this.kitQuantity = kitQuantity;
        this.materialInventory = materialInventory;
        this.materialName = materialName;
        this.numberofdistributions = numberofdistributions;
        this.quantity = quantity;
        this.unit = unit;
    }

    @JsonProperty("SourceId")
    public String getSourceId() {
        return sourceId;
    }

    @JsonProperty("SourceId")
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    @JsonProperty("RecordType")
    public String getRecordType() {
        return recordType;
    }

    @JsonProperty("RecordType")
    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    @JsonProperty("ContributedItem")
    public String getContributedItem() {
        return contributedItem;
    }

    @JsonProperty("ContributedItem")
    public void setContributedItem(String contributedItem) {
        this.contributedItem = contributedItem;
    }

    @JsonProperty("DistributedTo")
    public String getDistributedTo() {
        return distributedTo;
    }

    @JsonProperty("DistributedTo")
    public void setDistributedTo(String distributedTo) {
        this.distributedTo = distributedTo;
    }

    @JsonProperty("Eachlineitemquantity")
    public Long getEachlineitemquantity() {
        return eachlineitemquantity;
    }

    @JsonProperty("Eachlineitemquantity")
    public void setEachlineitemquantity(Long eachlineitemquantity) {
        this.eachlineitemquantity = eachlineitemquantity;
    }

    @JsonProperty("Kit")
    public String getKit() {
        return kit;
    }

    @JsonProperty("Kit")
    public void setKit(String kit) {
        this.kit = kit;
    }

    @JsonProperty("Kitlineitem")
    public String getKitlineitem() {
        return kitlineitem;
    }

    @JsonProperty("Kitlineitem")
    public void setKitlineitem(String kitlineitem) {
        this.kitlineitem = kitlineitem;
    }

    @JsonProperty("KitQuantity")
    public Long getKitQuantity() {
        return kitQuantity;
    }

    @JsonProperty("KitQuantity")
    public void setKitQuantity(Long kitQuantity) {
        this.kitQuantity = kitQuantity;
    }

    @JsonProperty("MaterialInventory")
    public String getMaterialInventory() {
        return materialInventory;
    }

    @JsonProperty("MaterialInventory")
    public void setMaterialInventory(String materialInventory) {
        this.materialInventory = materialInventory;
    }

    @JsonProperty("MaterialName")
    public String getMaterialName() {
        return materialName;
    }

    @JsonProperty("MaterialName")
    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    @JsonProperty("Numberofdistributions")
    public Long getNumberofdistributions() {
        return numberofdistributions;
    }

    @JsonProperty("Numberofdistributions")
    public void setNumberofdistributions(Long numberofdistributions) {
        this.numberofdistributions = numberofdistributions;
    }

    @JsonProperty("Quantity")
    public Long getQuantity() {
        return quantity;
    }

    @JsonProperty("Quantity")
    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    @JsonProperty("Unit")
    public String getUnit() {
        return unit;
    }

    @JsonProperty("Unit")
    public void setUnit(String unit) {
        this.unit = unit;
    }

}
