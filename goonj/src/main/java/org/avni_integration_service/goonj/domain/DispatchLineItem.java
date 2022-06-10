package org.avni_integration_service.goonj.domain;

import java.util.Objects;

/*
{
                "Unit": "Gunny Bags",
                "Type": null,
                "Quantity": 2.00,
                "PurchaseItemCategory": null,
                "OtherKitDetails": null,
                "MaterialName": null,
                "KitSubType": null,
                "KitName": null,
                "ItemName": null,
                "ItemCategory": "Clothes",
                "DispatchLineItemId": "a19C20000000GMXIA2",
                "ContributedItem": "shirt"
            }
 */
public class DispatchLineItem {
    private String Unit;
    private String Type;
    private String Quantity;
    private String PurchaseItemCategory;
    private String OtherKitDetails;
    private String MaterialName;
    private String KitSubType;
    private String KitName;
    private String ItemName;
    private String ItemCategory;
    private String DispatchLineItemId;
    private String ContributedItem;

    public DispatchLineItem(String unit, String type, String quantity, String purchaseItemCategory, String otherKitDetails,
                            String materialName, String kitSubType, String kitName, String itemName, String itemCategory,
                            String dispatchLineItemId, String contributedItem) {
        Unit = unit;
        Type = type;
        Quantity = quantity;
        PurchaseItemCategory = purchaseItemCategory;
        OtherKitDetails = otherKitDetails;
        MaterialName = materialName;
        KitSubType = kitSubType;
        KitName = kitName;
        ItemName = itemName;
        ItemCategory = itemCategory;
        DispatchLineItemId = dispatchLineItemId;
        ContributedItem = contributedItem;
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String unit) {
        Unit = unit;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getPurchaseItemCategory() {
        return PurchaseItemCategory;
    }

    public void setPurchaseItemCategory(String purchaseItemCategory) {
        PurchaseItemCategory = purchaseItemCategory;
    }

    public String getOtherKitDetails() {
        return OtherKitDetails;
    }

    public void setOtherKitDetails(String otherKitDetails) {
        OtherKitDetails = otherKitDetails;
    }

    public String getMaterialName() {
        return MaterialName;
    }

    public void setMaterialName(String materialName) {
        MaterialName = materialName;
    }

    public String getKitSubType() {
        return KitSubType;
    }

    public void setKitSubType(String kitSubType) {
        KitSubType = kitSubType;
    }

    public String getKitName() {
        return KitName;
    }

    public void setKitName(String kitName) {
        KitName = kitName;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public String getItemCategory() {
        return ItemCategory;
    }

    public void setItemCategory(String itemCategory) {
        ItemCategory = itemCategory;
    }

    public String getDispatchLineItemId() {
        return DispatchLineItemId;
    }

    public void setDispatchLineItemId(String dispatchLineItemId) {
        DispatchLineItemId = dispatchLineItemId;
    }

    public String getContributedItem() {
        return ContributedItem;
    }

    public void setContributedItem(String contributedItem) {
        ContributedItem = contributedItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DispatchLineItem that = (DispatchLineItem) o;
        return DispatchLineItemId.equals(that.DispatchLineItemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(DispatchLineItemId);
    }

    @Override
    public String toString() {
        return "DispatchLineItem{" +
                "Unit='" + Unit + '\'' +
                ", Type='" + Type + '\'' +
                ", Quantity='" + Quantity + '\'' +
                ", PurchaseItemCategory='" + PurchaseItemCategory + '\'' +
                ", OtherKitDetails='" + OtherKitDetails + '\'' +
                ", MaterialName='" + MaterialName + '\'' +
                ", KitSubType='" + KitSubType + '\'' +
                ", KitName='" + KitName + '\'' +
                ", ItemName='" + ItemName + '\'' +
                ", ItemCategory='" + ItemCategory + '\'' +
                ", DispatchLineItemId='" + DispatchLineItemId + '\'' +
                ", ContributedItem='" + ContributedItem + '\'' +
                '}';
    }
}
