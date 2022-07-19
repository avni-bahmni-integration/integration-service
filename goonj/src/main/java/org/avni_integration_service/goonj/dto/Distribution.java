
package org.avni_integration_service.goonj.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "SourceId",
    "Block",
    "DateOfDistribution",
    "DisasterType",
    "DispatchStatus",
    "District",
    "Duplicate",
    "LocalityVillageName",
    "NameofAccount",
    "PhotographInformation",
    "PictureStatus",
    "POCId",
    "Remarks",
    "State",
    "TypeofCommunity",
    "TypeofInitiative",
    "DistributionLines"
})
public class Distribution {

    @JsonProperty("SourceId")
    private String sourceId;
    @JsonProperty("Block")
    private String block;
    @JsonProperty("DateOfDistribution")
    private String dateOfDistribution;
    @JsonProperty("DisasterType")
    private String disasterType;
    @JsonProperty("DispatchStatus")
    private String dispatchStatus;
    @JsonProperty("District")
    private String district;
    @JsonProperty("Duplicate")
    private boolean duplicate;
    @JsonProperty("LocalityVillageName")
    private String localityVillageName;
    @JsonProperty("NameofAccount")
    private String nameofAccount;
    @JsonProperty("PhotographInformation")
    private String photographInformation;
    @JsonProperty("PictureStatus")
    private String pictureStatus;
    @JsonProperty("POCId")
    private String pOCId;
    @JsonProperty("Remarks")
    private String remarks;
    @JsonProperty("State")
    private String state;
    @JsonProperty("TypeofCommunity")
    private String typeofCommunity;
    @JsonProperty("TypeofInitiative")
    private String typeofInitiative;
    @JsonProperty("DistributionLines")
    private List<DistributionLine> distributionLines = new ArrayList<DistributionLine>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Distribution() {
    }

    /**
     * 
     * @param sourceId
     * @param distributionLines
     * @param disasterType
     * @param photographInformation
     * @param duplicate
     * @param pictureStatus
     * @param dispatchStatus
     * @param nameofAccount
     * @param localityVillageName
     * @param pOCId
     * @param typeofInitiative
     * @param dateOfDistribution
     * @param typeofCommunity
     * @param district
     * @param block
     * @param state
     * @param remarks
     */
    public Distribution(String sourceId, String block, String dateOfDistribution, String disasterType, String dispatchStatus, String district, boolean duplicate, String localityVillageName, String nameofAccount, String photographInformation, String pictureStatus, String pOCId, String remarks, String state, String typeofCommunity, String typeofInitiative, List<DistributionLine> distributionLines) {
        super();
        this.sourceId = sourceId;
        this.block = block;
        this.dateOfDistribution = dateOfDistribution;
        this.disasterType = disasterType;
        this.dispatchStatus = dispatchStatus;
        this.district = district;
        this.duplicate = duplicate;
        this.localityVillageName = localityVillageName;
        this.nameofAccount = nameofAccount;
        this.photographInformation = photographInformation;
        this.pictureStatus = pictureStatus;
        this.pOCId = pOCId;
        this.remarks = remarks;
        this.state = state;
        this.typeofCommunity = typeofCommunity;
        this.typeofInitiative = typeofInitiative;
        this.distributionLines = distributionLines;
    }

    @JsonProperty("SourceId")
    public String getSourceId() {
        return sourceId;
    }

    @JsonProperty("SourceId")
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    @JsonProperty("Block")
    public String getBlock() {
        return block;
    }

    @JsonProperty("Block")
    public void setBlock(String block) {
        this.block = block;
    }

    @JsonProperty("DateOfDistribution")
    public String getDateOfDistribution() {
        return dateOfDistribution;
    }

    @JsonProperty("DateOfDistribution")
    public void setDateOfDistribution(String dateOfDistribution) {
        this.dateOfDistribution = dateOfDistribution;
    }

    @JsonProperty("DisasterType")
    public String getDisasterType() {
        return disasterType;
    }

    @JsonProperty("DisasterType")
    public void setDisasterType(String disasterType) {
        this.disasterType = disasterType;
    }

    @JsonProperty("DispatchStatus")
    public String getDispatchStatus() {
        return dispatchStatus;
    }

    @JsonProperty("DispatchStatus")
    public void setDispatchStatus(String dispatchStatus) {
        this.dispatchStatus = dispatchStatus;
    }

    @JsonProperty("District")
    public String getDistrict() {
        return district;
    }

    @JsonProperty("District")
    public void setDistrict(String district) {
        this.district = district;
    }

    @JsonProperty("Duplicate")
    public boolean isDuplicate() {
        return duplicate;
    }

    @JsonProperty("Duplicate")
    public void setDuplicate(boolean duplicate) {
        this.duplicate = duplicate;
    }

    @JsonProperty("LocalityVillageName")
    public String getLocalityVillageName() {
        return localityVillageName;
    }

    @JsonProperty("LocalityVillageName")
    public void setLocalityVillageName(String localityVillageName) {
        this.localityVillageName = localityVillageName;
    }

    @JsonProperty("NameofAccount")
    public String getNameofAccount() {
        return nameofAccount;
    }

    @JsonProperty("NameofAccount")
    public void setNameofAccount(String nameofAccount) {
        this.nameofAccount = nameofAccount;
    }

    @JsonProperty("PhotographInformation")
    public String getPhotographInformation() {
        return photographInformation;
    }

    @JsonProperty("PhotographInformation")
    public void setPhotographInformation(String photographInformation) {
        this.photographInformation = photographInformation;
    }

    @JsonProperty("PictureStatus")
    public String getPictureStatus() {
        return pictureStatus;
    }

    @JsonProperty("PictureStatus")
    public void setPictureStatus(String pictureStatus) {
        this.pictureStatus = pictureStatus;
    }

    @JsonProperty("POCId")
    public String getPOCId() {
        return pOCId;
    }

    @JsonProperty("POCId")
    public void setPOCId(String pOCId) {
        this.pOCId = pOCId;
    }

    @JsonProperty("Remarks")
    public String getRemarks() {
        return remarks;
    }

    @JsonProperty("Remarks")
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @JsonProperty("State")
    public String getState() {
        return state;
    }

    @JsonProperty("State")
    public void setState(String state) {
        this.state = state;
    }

    @JsonProperty("TypeofCommunity")
    public String getTypeofCommunity() {
        return typeofCommunity;
    }

    @JsonProperty("TypeofCommunity")
    public void setTypeofCommunity(String typeofCommunity) {
        this.typeofCommunity = typeofCommunity;
    }

    @JsonProperty("TypeofInitiative")
    public String getTypeofInitiative() {
        return typeofInitiative;
    }

    @JsonProperty("TypeofInitiative")
    public void setTypeofInitiative(String typeofInitiative) {
        this.typeofInitiative = typeofInitiative;
    }

    @JsonProperty("DistributionLines")
    public List<DistributionLine> getDistributionLines() {
        return distributionLines;
    }

    @JsonProperty("DistributionLines")
    public void setDistributionLines(List<DistributionLine> distributionLines) {
        this.distributionLines = distributionLines;
    }

}
