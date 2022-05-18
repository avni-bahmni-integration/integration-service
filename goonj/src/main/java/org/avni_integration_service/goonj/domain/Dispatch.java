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

import java.util.Map;

public class Dispatch {
    public static Subject from(Map<String, Object> dispatch) {
        Subject subject = new Subject();
        subject.setSubjectType("Dispatch");
        subject.set(Subject.AddressFieldName, dispatch.get("foo"));
        dispatch.forEach((field, value) -> {
            if (!field.equals("DispatchLineItemId"))
                subject.addObservation(field, value);
        });
        return subject;
    }
}
