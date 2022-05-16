package org.avni_integration_service.goonj.domain;

/*
"Unit": "Nos",
        "Type": null,
        "TargetCommunity": "None",
        "Quantity": 1.00,
        "PurchaseItemCategory": "Miscellaneous",
        "OtherKitDetails": null,
        "MaterialName": "ACC-145988-P-Gunny Bags",
        "LocalDemand": "No",
        "KitSubType": null,
        "KitName": null,
        "ItemName": "Gunny Bags",
        "ItemCategory": "Repairable",
        "DispatchStatusName": "2022/KAR/ACC-145990/MHR/001",
        "DispatchStatusId": "a1AC20000005uG5MAI",
        "DispatchState": "Maharashtra",
        "DispatchLineItemId": "a19C20000000G81IAE",
        "DispatchDate": "28/04/2022",
        "DisasterType": "Not Applicable",
        "ContributedItem": null,
        "AccountName": "Mumbai"
 */

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class Dispatch {
    @JsonProperty("Unit")
    private String unit;

    @JsonProperty("Type")
    private String type;

    @JsonProperty("TargetCommunity")
    private String targetCommunity;

    @JsonProperty("Quantity")
    private int quantity;

    @JsonProperty("PurchaseItemCategory")
    private String purchaseItemCategory;

    @JsonProperty("OtherKitDetails")
    private String OtherKitDetails;

    @JsonProperty("MaterialName")
    private String materialName;

    @JsonProperty("LocalDemand")
    private String localDemand;

    @JsonProperty("KitSubType")
    private String kitSubType;

    @JsonProperty("KitName")
    private String kitName;

    @JsonProperty("ItemName")
    private String itemName;

    @JsonProperty("ItemCategory")
    private String itemCategory;

    @JsonProperty("DispatchStatusName")
    private String dispatchStatusName;

    @JsonProperty("DispatchStatusId")
    private String getDispatchStatusId;

    @JsonProperty("DispatchState")
    private String dispatchState;

    @JsonProperty("DispatchLineItemId")
    private String dispatchLineItemId;

    @JsonProperty("DispatchDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
    private LocalDate dispatchDate;

    @JsonProperty("DisasterType")
    private String disasterType;

    @JsonProperty("ContributedItem")
    private String contributedItem;

    @JsonProperty("AccountName")
    private String accountName;
}
