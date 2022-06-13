package org.avni_integration_service.goonj.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.util.MapUtil;

import java.util.Map;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Demand {
    private String accountName;
    private String demandName;
    private String demandId;
    private String typeOfDisaster;
    private String targetCommunity;
    private String state;
    private String district;
    private Integer numberOfPeople;

    public Demand(String accountName, String demandName, String demandId, String typeOfDisaster,
                  String targetCommunity, String state, String district, int numberOfPeople) {
        this.accountName = accountName;
        this.demandName = demandName;
        this.demandId = demandId;
        this.typeOfDisaster = typeOfDisaster;
        this.targetCommunity = targetCommunity;
        this.state = state;
        this.district = district;
        this.numberOfPeople = numberOfPeople;
    }

    public Demand() {
        super();
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getDemandName() {
        return demandName;
    }

    public void setDemandName(String demandName) {
        this.demandName = demandName;
    }

    public String getDemandId() {
        return demandId;
    }

    public void setDemandId(String demandId) {
        this.demandId = demandId;
    }

    public String getTypeOfDisaster() {
        return typeOfDisaster;
    }

    public void setTypeOfDisaster(String typeOfDisaster) {
        this.typeOfDisaster = typeOfDisaster;
    }

    public String getTargetCommunity() {
        return targetCommunity;
    }

    public void setTargetCommunity(String targetCommunity) {
        this.targetCommunity = targetCommunity;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(Integer numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Demand demand = (Demand) o;
        return Objects.equals(accountName, demand.accountName) && Objects.equals(demandName, demand.demandName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountName, demandName);
    }

    @Override
    public String toString() {
        return "Demand{" +
                "AccountName='" + accountName + '\'' +
                ", DemandName='" + demandName + '\'' +
                ", DemandId='" + demandId + '\'' +
                ", TypeOfDisaster='" + typeOfDisaster + '\'' +
                ", TargetCommunity='" + targetCommunity + '\'' +
                ", State='" + state + '\'' +
                ", District='" + district + '\'' +
                ", NumberOfPeople=" + numberOfPeople +
                '}';
    }

    public static Demand from(Map<String, Object> demandResponse) {
        Demand demand = new Demand();
        demand.setTypeOfDisaster(MapUtil.getString("TypeOfDisaster", demandResponse));
        demand.setTargetCommunity(MapUtil.getString("TargetCommunity", demandResponse));
        demand.setState((String) demandResponse.get("DemandState"));
        demand.setNumberOfPeople(MapUtil.getInt("NumberOfPeople", demandResponse));
        demand.setDistrict(MapUtil.getString("DemandDistrict", demandResponse));
        demand.setAccountName((String) demandResponse.get("AccountName"));
        demand.setDemandId((String) demandResponse.get("DemandId"));
        demand.setDemandName((String) demandResponse.get("DemandName"));
        return demand;
    }

    public Subject fromSubject() {
        Subject subject = new Subject();
        subject.setSubjectType("Demand");
        subject.setAddress(this.getState());
        subject.setExternalId(this.getDemandName());

//        subject.addObservation("Type of Disaster", demandDto.getTypeOfDisaster());
//        subject.addObservation("Target Community", demandDto.getTargetCommunity());
//        subject.addObservation("Number of people", this.getNumberOfPeople());
//        subject.addObservation("Account Name", this.getAccountName());
//        subject.addObservation("DemandId", demandDto.getDemandId());
//        subject.addObservation("demandName", demandDto.getDemandName());
//        subject.addObservation("District", demandDto.getDistrict());
        return subject;
    }
}
