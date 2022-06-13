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

import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.util.MapUtil;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/*
[
    {
        "TargetCommunity": "Adolescents",
        "LocalDemand": "Yes",
        "DispatchStatusName": "2022//ACC-145988/KAR",
        "DispatchStatusId": "a1AC2000000614fMAA",
        "DispatchState": "Karnataka",
        "DispatchLineItems": [
            {
                "Unit": "Cartons",
                "Type": null,
                "Quantity": 2.00,
                "PurchaseItemCategory": null,
                "OtherKitDetails": null,
                "MaterialName": null,
                "KitSubType": null,
                "KitName": null,
                "ItemName": null,
                "ItemCategory": "Clothes",
                "DispatchLineItemId": "a19C20000000GKvIAM",
                "ContributedItem": "clothes"
            },
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
        ],
        "DispatchDistrict": "Bagalkot",
        "DispatchDate": "29/04/2022",
        "DisasterType": "Not Applicable",
        "Demand": "2022/ACC-145988/KAR/003",
        "AccountName": "GoonjTESTPC",
        "AccountCode": "ACC-145988"
    }
]
 */
public class Dispatch {

    private String AccountName;
    private String AccountCode;
    private String DemandName;
    private String DispatchDate;
    private String DispatchDistrict;
    private String DispatchStatusName;
    private String DispatchStatusId;
    private String DispatchState;
    private String DisasterType;
    private String LocalDemand;
    private String TypeOfDisaster;
    private String TargetCommunity;
    private String State;
    private String District;
    private int NumberOfPeople;
    private List<DispatchLineItem> dispatchLineItems;

    public static Subject from(Map<String, Object> dispatch) {
        Subject subject = new Subject();
        subject.setSubjectType("Dispatch");
        subject.setAddress(MapUtil.getString("foo", dispatch));
        dispatch.forEach((field, value) -> {
            if (!field.equals("DispatchLineItemId"))
                subject.addObservation(field, value);
        });
        return subject;
    }

    public Dispatch(String accountName, String accountCode, String demandName, String dispatchDate,
                    String dispatchDistrict, String dispatchStatusName, String dispatchStatusId, String dispatchState,
                    String disasterType, String localDemand, String typeOfDisaster, String targetCommunity,
                    String state, String district, int numberOfPeople, List<DispatchLineItem> dispatchLineItems) {
        AccountName = accountName;
        AccountCode = accountCode;
        DemandName = demandName;
        DispatchDate = dispatchDate;
        DispatchDistrict = dispatchDistrict;
        DispatchStatusName = dispatchStatusName;
        DispatchStatusId = dispatchStatusId;
        DispatchState = dispatchState;
        DisasterType = disasterType;
        LocalDemand = localDemand;
        TypeOfDisaster = typeOfDisaster;
        TargetCommunity = targetCommunity;
        State = state;
        District = district;
        NumberOfPeople = numberOfPeople;
        this.dispatchLineItems = dispatchLineItems;
    }

    public String getAccountName() {
        return AccountName;
    }

    public void setAccountName(String accountName) {
        AccountName = accountName;
    }

    public String getAccountCode() {
        return AccountCode;
    }

    public void setAccountCode(String accountCode) {
        AccountCode = accountCode;
    }

    public String getDemandName() {
        return DemandName;
    }

    public void setDemandName(String demandName) {
        DemandName = demandName;
    }

    public String getDispatchDate() {
        return DispatchDate;
    }

    public void setDispatchDate(String dispatchDate) {
        DispatchDate = dispatchDate;
    }

    public String getDispatchDistrict() {
        return DispatchDistrict;
    }

    public void setDispatchDistrict(String dispatchDistrict) {
        DispatchDistrict = dispatchDistrict;
    }

    public String getDispatchStatusName() {
        return DispatchStatusName;
    }

    public void setDispatchStatusName(String dispatchStatusName) {
        DispatchStatusName = dispatchStatusName;
    }

    public String getDispatchStatusId() {
        return DispatchStatusId;
    }

    public void setDispatchStatusId(String dispatchStatusId) {
        DispatchStatusId = dispatchStatusId;
    }

    public String getDispatchState() {
        return DispatchState;
    }

    public void setDispatchState(String dispatchState) {
        DispatchState = dispatchState;
    }

    public String getDisasterType() {
        return DisasterType;
    }

    public void setDisasterType(String disasterType) {
        DisasterType = disasterType;
    }

    public String getLocalDemand() {
        return LocalDemand;
    }

    public void setLocalDemand(String localDemand) {
        LocalDemand = localDemand;
    }

    public String getTypeOfDisaster() {
        return TypeOfDisaster;
    }

    public void setTypeOfDisaster(String typeOfDisaster) {
        TypeOfDisaster = typeOfDisaster;
    }

    public String getTargetCommunity() {
        return TargetCommunity;
    }

    public void setTargetCommunity(String targetCommunity) {
        TargetCommunity = targetCommunity;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getDistrict() {
        return District;
    }

    public void setDistrict(String district) {
        District = district;
    }

    public int getNumberOfPeople() {
        return NumberOfPeople;
    }

    public void setNumberOfPeople(int numberOfPeople) {
        NumberOfPeople = numberOfPeople;
    }

    public List<DispatchLineItem> getDispatchLineItems() {
        return dispatchLineItems;
    }

    public void setDispatchLineItems(List<DispatchLineItem> dispatchLineItems) {
        this.dispatchLineItems = dispatchLineItems;
    }

    public boolean isLocalDemand() {
        return getLocalDemand().equalsIgnoreCase("Yes");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dispatch dispatch = (Dispatch) o;
        return AccountName.equals(dispatch.AccountName) && DispatchStatusId.equals(dispatch.DispatchStatusId) && DispatchState.equals(dispatch.DispatchState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(AccountName, DispatchStatusId, DispatchState);
    }

    @Override
    public String toString() {
        return "Dispatch{" +
                "AccountName='" + AccountName + '\'' +
                ", AccountCode='" + AccountCode + '\'' +
                ", DemandName='" + DemandName + '\'' +
                ", DispatchDate='" + DispatchDate + '\'' +
                ", DispatchDistrict='" + DispatchDistrict + '\'' +
                ", DispatchStatusName='" + DispatchStatusName + '\'' +
                ", DispatchStatusId='" + DispatchStatusId + '\'' +
                ", DispatchState='" + DispatchState + '\'' +
                ", DisasterType='" + DisasterType + '\'' +
                ", LocalDemand='" + LocalDemand + '\'' +
                ", TypeOfDisaster='" + TypeOfDisaster + '\'' +
                ", TargetCommunity='" + TargetCommunity + '\'' +
                ", State='" + State + '\'' +
                ", District='" + District + '\'' +
                ", NumberOfPeople=" + NumberOfPeople +
                ", dispatchLineItems=" + dispatchLineItems +
                '}';
    }
}
