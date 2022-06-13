package org.avni_integration_service.goonj.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.avni_integration_service.avni.domain.Subject;

import java.util.Map;
import java.util.Objects;

/*
[
    {
        "TypeOfDisaster": "Not Applicable",
        "TargetCommunity": "None",
        "State": "Maharashtra",
        "NumberOfPeople": null,
        "District": "Mumbai City",
        "DemandName": "2022/ACC-145990/MHR/027",
        "DemandId": "a1CC20000007q6jMAA",
        "AccountName": "Mumbai"
    }
]
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Demand {
    private String AccountName;
    private String DemandName;
    private String DemandId;
    private String TypeOfDisaster;
    private String TargetCommunity;
    private String State;
    private String District;
    private int NumberOfPeople;

    public Demand(String accountName, String demandName, String demandId, String typeOfDisaster,
                  String targetCommunity, String state, String district, int numberOfPeople) {
        AccountName = accountName;
        DemandName = demandName;
        DemandId = demandId;
        TypeOfDisaster = typeOfDisaster;
        TargetCommunity = targetCommunity;
        State = state;
        District = district;
        NumberOfPeople = numberOfPeople;
    }

    public Demand() {
        super();
    }

    public String getAccountName() {
        return AccountName;
    }

    public void setAccountName(String accountName) {
        AccountName = accountName;
    }

    public String getDemandName() {
        return DemandName;
    }

    public void setDemandName(String demandName) {
        DemandName = demandName;
    }

    public String getDemandId() {
        return DemandId;
    }

    public void setDemandId(String demandId) {
        DemandId = demandId;
    }

    public String getTypeOfDisaster() {
        return TypeOfDisaster;
    }

    public void setTypeOfDisaster(String typeOfDisaster) {
        TypeOfDisaster = typeOfDisaster;
    }

    public String getTargetCommunity() {
        return TargetCommunity;
    }

    public void setTargetCommunity(String targetCommunity) {
        TargetCommunity = targetCommunity;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getDistrict() {
        return District;
    }

    public void setDistrict(String district) {
        District = district;
    }

    public int getNumberOfPeople() {
        return NumberOfPeople;
    }

    public void setNumberOfPeople(int numberOfPeople) {
        NumberOfPeople = numberOfPeople;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Demand demand = (Demand) o;
        return Objects.equals(AccountName, demand.AccountName) && Objects.equals(DemandName, demand.DemandName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(AccountName, DemandName);
    }

    @Override
    public String toString() {
        return "Demand{" +
                "AccountName='" + AccountName + '\'' +
                ", DemandName='" + DemandName + '\'' +
                ", DemandId='" + DemandId + '\'' +
                ", TypeOfDisaster='" + TypeOfDisaster + '\'' +
                ", TargetCommunity='" + TargetCommunity + '\'' +
                ", State='" + State + '\'' +
                ", District='" + District + '\'' +
                ", NumberOfPeople=" + NumberOfPeople +
                '}';
    }

    public static Demand from(Map<String, Object> demand) {
        Demand demandDTO = new Demand();
        demandDTO.setAccountName((String) demand.get("AccountName"));
        demandDTO.setDemandName((String) demand.get("DemandName"));
        demandDTO.setDemandId((String) demand.get("DemandId"));
        demandDTO.setTypeOfDisaster((String) demand.get("TypeOfDisaster"));
        demandDTO.setState((String) demand.get("State"));
        demandDTO.setDistrict((String) demand.get("District"));
        demandDTO.setNumberOfPeople((Integer) demand.get("NumberOfPeople"));
        return demandDTO;
    }

    public static Subject subjectFrom(Demand demandDto) {
        Subject subject = new Subject();
        subject.setSubjectType("Demand");
        subject.setVoided(false);
        subject.set("legacyId", demandDto.getDemandName());
        subject.set("accountName", demandDto.getAccountName());
        subject.set("demandId", demandDto.getDemandId());
        subject.set("demandName", demandDto.getDemandName());
        subject.set("state", demandDto.getState());
        subject.set("district", demandDto.getDistrict());
        subject.set("numberOfPeople", demandDto.getNumberOfPeople());
        subject.set("targetCommunity", demandDto.getTargetCommunity());
        subject.set("typeOfDisaster", demandDto.getTypeOfDisaster());
        return subject;
    }

}
