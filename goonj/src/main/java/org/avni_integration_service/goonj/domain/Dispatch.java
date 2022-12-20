package org.avni_integration_service.goonj.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.avni_integration_service.avni.domain.GeneralEncounter;
import org.avni_integration_service.goonj.GoonjEntityType;
import org.avni_integration_service.goonj.util.DateTimeUtil;
import org.avni_integration_service.util.MapUtil;

import java.util.*;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Dispatch implements GoonjEntity {

    private static final String DispatchDateField = "DispatchDate";
    private static final String DemandIdField = "DemandId";
    private static final String DispatchLineItemsField = "DispatchLineItems";
    private static final String DispatchStatusIdField = "DispatchStatusId"; //2 mappings, 1 as setExternalID and another as Dispatch Status Id concept
    private static final List<String> Core_Fields = Arrays.asList(DispatchDateField, DemandIdField, DispatchLineItemsField);

    public static final String LAST_UPDATED_DATE_TIME = "LastUpdatedDateTime";
    public static final String TARGET_COMMUNITY = "TargetCommunity";
    public static final String LOCAL_DEMAND = "LocalDemand";
    public static final String DISASTER_TYPE = "DisasterType";
    public static final String DEMAND = "Demand";
    public static final String ACCOUNT_ID = "AccountId";
    public static final String ACCOUNT_NAME = "AccountName";
    public static final String ACCOUNT_CODE = "AccountCode";
    public static final String DISPATCH_STATE = "DispatchState";
    public static final String DISPATCH_DISTRICT = "DispatchDistrict";
    private static final List<String> Ignored_Fields = Arrays.asList(LAST_UPDATED_DATE_TIME, TARGET_COMMUNITY,
            LOCAL_DEMAND, DISASTER_TYPE, DEMAND, ACCOUNT_ID, ACCOUNT_NAME, ACCOUNT_CODE, DISPATCH_STATE, DISPATCH_DISTRICT);

    private Map<String, Object> response;

    public static Dispatch from(Map<String, Object> dispatchResponse) {
        Dispatch dispatch = new Dispatch();
        dispatch.response = dispatchResponse;
        return dispatch;
    }

    public GeneralEncounter mapToAvniEncounter() {
        GeneralEncounter encounterRequest = new GeneralEncounter();
        encounterRequest.setSubjectExternalID(MapUtil.getString(DemandIdField, response));
        encounterRequest.setExternalID(MapUtil.getString(DispatchStatusIdField, response));
        encounterRequest.setEncounterType(GoonjEntityType.Dispatch.getDbName());
        Date dispatchDate = DateTimeUtil.convertToDateFromGoonjDateString(MapUtil.getString(DispatchDateField, response));
        dispatchDate = DateTimeUtil.offsetTimeZone(dispatchDate, DateTimeUtil.IST, DateTimeUtil.UTC);
        encounterRequest.setEncounterDateTime(dispatchDate);
        encounterRequest.setObservations(new LinkedHashMap<>());
        encounterRequest.setVoided(false);
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
