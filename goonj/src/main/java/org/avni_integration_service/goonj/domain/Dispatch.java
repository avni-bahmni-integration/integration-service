package org.avni_integration_service.goonj.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.avni_integration_service.avni.domain.GeneralEncounter;
import org.avni_integration_service.util.FormatAndParseUtil;
import org.avni_integration_service.util.MapUtil;

import java.util.*;
import java.util.stream.Collectors;

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
@JsonIgnoreProperties(ignoreUnknown = true)
public class Dispatch {

    private static final String DemandField = "Demand";
    private static final String DispatchStatusNameField = "DispatchStatusName";
    private static final String DemandIsVoidedField = "IsVoided";
    private static final List<String> Core_Fields = Arrays.asList(DemandField, DispatchStatusNameField, DemandIsVoidedField);

    private Map<String, Object> response;

    public static Dispatch from(Map<String, Object> dispatchResponse) {
        Dispatch dispatch = new Dispatch();
        dispatch.response = dispatchResponse;
        return dispatch;
    }

    public GeneralEncounter mapToAvniEncounter() {
        GeneralEncounter encounterRequest = new GeneralEncounter();
        //TODO Use Subject External Id
        encounterRequest.setSubjectExternalID(MapUtil.getString(DemandField, response));
        encounterRequest.setExternalID(MapUtil.getString(DispatchStatusNameField, response));
        encounterRequest.setEncounterType("Dispatch");
        encounterRequest.setEncounterDateTime(FormatAndParseUtil.now());
        encounterRequest.setObservations(new LinkedHashMap<>());
        encounterRequest.setVoided(MapUtil.getBoolean(DemandIsVoidedField, response));
        encounterRequest.set("cancelObservations", new HashMap<>());
        return encounterRequest;
    }

    public List<String> getObservationFields() {
        return response.keySet().stream().filter(s -> !Core_Fields.contains(s)).collect(Collectors.toList());
    }

    public Object getValue(String responseField) {
        return this.response.get(responseField);
    }
}
