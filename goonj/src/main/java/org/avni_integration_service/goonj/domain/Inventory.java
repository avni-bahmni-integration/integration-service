package org.avni_integration_service.goonj.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.goonj.util.DateTimeUtil;
import org.avni_integration_service.util.MapUtil;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Inventory implements GoonjEntity {

    private static final String InventoryStateField = "CenterFieldOfficeState";
    private static final String InventoryDistrictField = "CenterFieldOfficeDistrict";
    private static final String InventoryLastModifiedBy = "LastModifiedBy";
    private static final String InventoryNameField = "ImplementationInventoryName";
    private static final String InventoryIdField = "ImplementationInventoryId";
    private static final String InventoryDateField = "LastModifiedDate";
    private static final String InventoryCreatedBy = "CreatedBy";
    private static final String InventoryIsVoidedField = "IsVoided";
    private static final String InventorySourceOfMaterialField = "SourceOfMaterial";
    private static final List<String> Core_Fields = Arrays.asList(InventoryStateField, InventoryDistrictField,
            InventoryLastModifiedBy, InventoryNameField, InventoryIdField, InventoryDateField,
            InventoryCreatedBy, InventoryIsVoidedField);
    private static final List<String> Ignored_Fields = Arrays.asList(InventorySourceOfMaterialField);
    public static final String KIT = "Kit";
    public static final String TYPE_OF_MATERIAL = "Type Of Material";
    public static final String PURCHASED = "Purchased";
    public static final String PURCHASED_ITEM = "Purchased item";
    public static final String CONTRIBUTED = "Contributed";
    public static final String CONTRIBUTED_ITEM = "Contributed item";
    public static final String CONTRIBUTED_TRACK = "Contributed_Track";
    public static final String CONTRIBUTED_TRACK1 = "Contributed track";
    public static final String GOONJ_PRODUCT = "Goonj Product";
    public static final String GOONJ_PRODUCT1 = "Goonj product";
    private Map<String, Object> response;

    public static Inventory from(Map<String, Object> dispatchResponse) {
        Inventory dispatch = new Inventory();
        dispatch.response = dispatchResponse;
        return dispatch;
    }

    public Subject subjectWithoutObservations() {
        Subject subject = new Subject();
        subject.setSubjectType("Inventory Item");
        Date InventoryDate = DateTimeUtil.offsetTimeZone(new Date(), DateTimeUtil.UTC, DateTimeUtil.IST);
        subject.setRegistrationDate(InventoryDate);
        subject.setAddress(MapUtil.getString(InventoryStateField, response) +", "+MapUtil.getString(InventoryDistrictField, response));
        subject.setFirstName(MapUtil.getString(InventoryNameField, response));
        subject.setExternalId(MapUtil.getString(InventoryIdField, response));
        subject.setVoided(MapUtil.getBoolean(InventoryIsVoidedField, response));
        initializeSourceOfMaterial(subject);
        return subject;
    }

    private void initializeSourceOfMaterial(Subject subject) {
        String sourceOfMaterial = MapUtil.getString(InventorySourceOfMaterialField, response);
        if (!StringUtils.hasText(sourceOfMaterial)) {
            sourceOfMaterial = KIT;
        } else if (sourceOfMaterial.equals(PURCHASED)) {
            sourceOfMaterial = PURCHASED_ITEM;
        }
        else if (sourceOfMaterial.equals(CONTRIBUTED)) {
            sourceOfMaterial = CONTRIBUTED_ITEM;
        }
        else if (sourceOfMaterial.equals(CONTRIBUTED_TRACK)) {
            sourceOfMaterial = CONTRIBUTED_TRACK1;
        }
        else if (sourceOfMaterial.equals(GOONJ_PRODUCT)) {
            sourceOfMaterial = GOONJ_PRODUCT1;
        }
        subject.addObservation(TYPE_OF_MATERIAL, sourceOfMaterial);
    }

    @Override
    public List<String> getObservationFields() {
        return response.keySet().stream().filter(s -> !Core_Fields.contains(s) && !Ignored_Fields.contains(s)).collect(Collectors.toList());
    }

    @Override
    public Object getValue(String responseField) {
        return this.response.get(responseField);
    }

}
