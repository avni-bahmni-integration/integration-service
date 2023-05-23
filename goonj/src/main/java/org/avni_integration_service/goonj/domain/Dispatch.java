package org.avni_integration_service.goonj.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.goonj.util.DateTimeUtil;
import org.avni_integration_service.util.MapUtil;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Dispatch implements GoonjEntity {

    private static final String DispatchNameField = "DispatchStatusName";
    private static final String DispatchStateField = "DispatchState";
    private static final String DispatchDistrictField = "DispatchDistrict";
    private static final String DispatchIsVoidedField = "IsVoided";
    private static final String DispatchDateField = "DispatchDate";
    private static final String DemandIdField = "DemandId";
    private static final String DispatchLineItemsField = "DispatchLineItems";
    private static final String DispatchStatusIdField = "DispatchStatusId"; //2 mappings, 1 as setExternalID and another as Dispatch Status Id concept

    public static final String LAST_UPDATED_DATE_TIME = "LastUpdatedDateTime";
    public static final String TARGET_COMMUNITY = "TargetCommunity";
    public static final String LOCAL_DEMAND = "LocalDemand";
    public static final String DISASTER_TYPE = "DisasterType";
    public static final String DEMAND = "Demand";
    public static final String ACCOUNT_ID = "AccountId";
    public static final String ACCOUNT_NAME = "AccountName";
    private static final List<String> Core_Fields = Arrays.asList(DispatchDateField, DispatchLineItemsField);
    public static final String ACCOUNT_CODE = "AccountCode";
    public static final String DISPATCH_STATE = "DispatchState";
    public static final String DISPATCH_DISTRICT = "DispatchDistrict";
    private static final List<String> Ignored_Fields = Arrays.asList(LAST_UPDATED_DATE_TIME, TARGET_COMMUNITY,
            LOCAL_DEMAND, DISASTER_TYPE, DEMAND, ACCOUNT_ID, ACCOUNT_CODE, DISPATCH_STATE, DISPATCH_DISTRICT);

    private Map<String, Object> response;

    public static Dispatch from(Map<String, Object> dispatchResponse) {
        Dispatch dispatch = new Dispatch();
        dispatch.response = dispatchResponse;
        return dispatch;
    }

    public Subject subjectWithoutObservations() {
        Subject subject = new Subject();
        subject.setSubjectType("Dispatch");
        Date dispatchDate = DateTimeUtil.convertToDateFromGoonjDateString(MapUtil.getString(DispatchDateField, response));
//        dispatchDate = DateTimeUtil.offsetTimeZone(dispatchDate, DateTimeUtil.UTC, DateTimeUtil.IST);
        subject.setRegistrationDate(dispatchDate);
        subject.setAddress(MapUtil.getString(DispatchStateField, response) +", "+MapUtil.getString(DispatchDistrictField, response));
        subject.setExternalId(MapUtil.getString(DispatchStatusIdField, response));
        subject.setFirstName(MapUtil.getString(DispatchNameField, response));
        subject.setVoided(MapUtil.getBoolean(DispatchIsVoidedField, response));
        return subject;
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
