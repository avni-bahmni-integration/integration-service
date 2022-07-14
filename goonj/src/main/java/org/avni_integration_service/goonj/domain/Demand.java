package org.avni_integration_service.goonj.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.util.MapUtil;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Demand implements GoonjEntity{
    private static final String DemandDistrictField = "District";
    private static final String DemandStateField = "State";
    private static final String DemandNameField = "DemandName";
    private static final String DemandIdField = "DemandId";
    private static final String DemandIsVoidedField = "IsVoided";

    private Map<String, Object> response;

    private static final List<String> Core_Fields = Arrays.asList(DemandNameField, DemandDistrictField,
            DemandStateField, DemandIsVoidedField);

    public static Demand from(Map<String, Object> demandResponse) {
        Demand demand = new Demand();
        demand.response = demandResponse;
        return demand;
    }

    public Subject subjectWithoutObservations() {
        Subject subject = new Subject();
        subject.setSubjectType("Demand");
        subject.setRegistrationDate(new Date());
        subject.setAddress(MapUtil.getString(DemandStateField, response) +", "+MapUtil.getString(DemandDistrictField, response));
        subject.setExternalId(MapUtil.getString(DemandIdField, response));
        subject.setFirstName(MapUtil.getString(DemandNameField, response));
        subject.setVoided(MapUtil.getBoolean(DemandIsVoidedField, response));
//        subject.addObservation("Type of Disaster", demandDto.getTypeOfDisaster());
//        subject.addObservation("Target Community", demandDto.getTargetCommunity());
//        subject.addObservation("Number of people", this.getNumberOfPeople());
//        subject.addObservation("Account Name", this.getAccountName());
//        subject.addObservation("AccountId", this.getAccountId());
//        subject.addObservation("DemandId", demandDto.getDemandId());
//        subject.addObservation("demandName", demandDto.getDemandName());
//        subject.addObservation("District", demandDto.getDistrict());
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
