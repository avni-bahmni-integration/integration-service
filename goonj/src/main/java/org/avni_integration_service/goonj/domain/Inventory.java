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
public class Inventory implements GoonjEntity {

    private static final String InventoryStateField = "State";
    private static final String InventoryLastModifiedBy = "LastModifiedBy";
    private static final String InventoryNameField = "ImplementationInventoryName";
    private static final String InventoryIdField = "ImplementationInventoryId";
    private static final String InventoryDateField = "DateOfReceiving";
    private static final String InventoryCreatedBy = "CreatedBy";
    private static final String InventoryIsVoidedField = "IsVoided";
    private static final List<String> Core_Fields = Arrays.asList(InventoryStateField,
            InventoryLastModifiedBy, InventoryNameField, InventoryIdField, InventoryDateField,
            InventoryCreatedBy, InventoryIsVoidedField);
    private Map<String, Object> response;

    public static Inventory from(Map<String, Object> dispatchResponse) {
        Inventory dispatch = new Inventory();
        dispatch.response = dispatchResponse;
        return dispatch;
    }

    public Subject subjectWithoutObservations() {
        Subject subject = new Subject();
        subject.setSubjectType("Inventory Item");
        Date InventoryDate = DateTimeUtil.convertToDateFromGoonjDateString(MapUtil.getString(InventoryDateField, response));
        InventoryDate = DateTimeUtil.offsetTimeZone(InventoryDate, DateTimeUtil.IST, DateTimeUtil.UTC);
        subject.setRegistrationDate(InventoryDate);
        subject.setAddress(MapUtil.getString(InventoryStateField, response));
        subject.setFirstName(MapUtil.getString(InventoryNameField, response));
        subject.setVoided(MapUtil.getBoolean(InventoryIsVoidedField, response));
        return subject;
    }

    @Override
    public List<String> getObservationFields() {
        return response.keySet().stream().filter(s -> !Core_Fields.contains(s)).collect(Collectors.toList());
    }

    @Override
    public Object getValue(String responseField) {
        return this.response.get(responseField);
    }

}
