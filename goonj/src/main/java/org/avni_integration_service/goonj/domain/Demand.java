package org.avni_integration_service.goonj.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.util.MapUtil;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Demand {
    private static final String DemandStateField = "DemandState";
    private static final String DemandNameField = "DemandName";

    private String demandName;
    private String state;
    private Map<String, Object> response;

    public Demand() {
        super();
    }

    public String getDemandName() {
        return demandName;
    }

    public void setDemandName(String demandName) {
        this.demandName = demandName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public static Demand from(Map<String, Object> demandResponse) {
        Demand demand = new Demand();
        demand.response = demandResponse;
        return demand;
    }

    public Subject subjectWithoutObservations() {
        Subject subject = new Subject();
        subject.setSubjectType("Demand");
        subject.setAddress(MapUtil.getString("DemandState", response));
        subject.setExternalId(MapUtil.getString("DemandName", response));

//        subject.addObservation("Type of Disaster", demandDto.getTypeOfDisaster());
//        subject.addObservation("Target Community", demandDto.getTargetCommunity());
//        subject.addObservation("Number of people", this.getNumberOfPeople());
//        subject.addObservation("Account Name", this.getAccountName());
//        subject.addObservation("DemandId", demandDto.getDemandId());
//        subject.addObservation("demandName", demandDto.getDemandName());
//        subject.addObservation("District", demandDto.getDistrict());
        return subject;
    }

    public List<String> getObservationFields() {
        return response.keySet().stream().filter(s -> !s.equals(DemandNameField) && !s.equals(DemandStateField)).collect(Collectors.toList());
    }

    public Object getValue(String responseField) {
        return this.response.get(responseField);
    }
}
