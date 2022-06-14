package org.avni_integration_service.goonj.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.avni_integration_service.avni.domain.GeneralEncounter;
import org.avni_integration_service.util.FormatAndParseUtil;
import org.avni_integration_service.util.MapUtil;

import java.util.*;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Dispatch implements GoonjEntity {

    private static final String DemandField = "Demand";
    private static final String DispatchLineItemsField = "DispatchLineItems";
    private static final String DispatchStatusNameField = "DispatchStatusName";
    private static final String DemandIsVoidedField = "IsVoided";
    private static final List<String> Core_Fields = Arrays.asList(DemandField, DispatchStatusNameField, DemandIsVoidedField, DispatchLineItemsField);
    private static final List<String> Ignored_Fields = Arrays.asList("TargetCommunity", "DispatchDistrict", "DispatchState", "LocalDemand", "DisasterType", "AccountName", "AccountCode");

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

    @Override
    public List<String> getObservationFields() {
        return response.keySet().stream().filter(s -> !Core_Fields.contains(s) && !Ignored_Fields.contains(s)).collect(Collectors.toList());
    }

    @Override
    public Object getValue(String responseField) {
        return this.response.get(responseField);
    }

    public List<DispatchLineItem> getLineItems() {
        List<Map<String, Object>> lineItemList = (List<Map<String, Object>>) response.get(DispatchLineItemsField);
        return lineItemList.stream().map(DispatchLineItem::new).collect(Collectors.toList());
    }
}
