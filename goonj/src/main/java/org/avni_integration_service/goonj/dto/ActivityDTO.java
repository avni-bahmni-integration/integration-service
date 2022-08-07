package org.avni_integration_service.goonj.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
    "SourceId",
    "ActivityCategory",
    "ActivityConductedWithStudents",
    "ActivityEndDate",
    "ActivityStartDate",
    "ActivitySubType",
    "ActivityType",
    "Block",
    "Breadth",
    "DepthHeight",
    "Diameter",
    "Demand",
    "DistributionSourceId",
    "District",
    "Length",
    "LocalityVillageName",
    "MeasurementType",
    "NoofdaysofParticipationNJPC",
    "NoofdaysofParticipationS2S",
    "NoofparticipantsFemaleDFW",
    "NoofparticipantsFemaleNJPC",
    "NoofparticipantsFemaleS2S",
    "NoofparticipantsMaleDFW",
    "NoofparticipantsMaleNJPC",
    "NoofparticipantsMaleS2S",
    "NoofparticipantsNJPC",
    "NoofparticipantsS2S",
    "NoofWorkingDays",
    "Nos",
    "ObjectiveofDFWwork",
        "OtherObjective",
        "OtherSubType",
    "SchoolAanganwadiLearningCenterName",
    "State",
    "TargetCommunity",
    "TypeofInitiative",
        "CreatedBy",
        "ModifiedBy"
})
public class ActivityDTO {

    @JsonProperty("SourceId")
    private String sourceId;
    @JsonProperty("ActivityCategory")
    private String activityCategory;
    @JsonProperty("ActivityConductedWithStudents")
    private String activityConductedWithStudents;
    @JsonProperty("ActivityEndDate")
    private String activityEndDate;
    @JsonProperty("ActivityStartDate")
    private String activityStartDate;
    @JsonProperty("ActivitySubType")
    private String activitySubType;
    @JsonProperty("ActivityType")
    private String activityType;
    @JsonProperty("Block")
    private String block;
    @JsonProperty("Breadth")
    private Long breadth;
    @JsonProperty("DepthHeight")
    private Long depthHeight;
    @JsonProperty("Diameter")
    private Long diameter;
    @JsonProperty("Demand")
    private String demand;
    @JsonProperty("DistributionSourceId")
    private String distribution;
    @JsonProperty("District")
    private String district;
    @JsonProperty("Length")
    private Long length;
    @JsonProperty("LocalityVillageName")
    private String localityVillageName;
    @JsonProperty("MeasurementType")
    private String measurementType;
    @JsonProperty("NoofdaysofParticipationNJPC")
    private Long noofdaysofParticipationNJPC;
    @JsonProperty("NoofdaysofParticipationS2S")
    private Long noofdaysofParticipationS2S;
    @JsonProperty("NoofparticipantsFemaleDFW")
    private Long noofparticipantsFemaleDFW;
    @JsonProperty("NoofparticipantsFemaleNJPC")
    private Long noofparticipantsFemaleNJPC;
    @JsonProperty("NoofparticipantsFemaleS2S")
    private Long noofparticipantsFemaleS2S;
    @JsonProperty("NoofparticipantsMaleDFW")
    private Long noofparticipantsMaleDFW;
    @JsonProperty("NoofparticipantsMaleNJPC")
    private Long noofparticipantsMaleNJPC;
    @JsonProperty("NoofparticipantsMaleS2S")
    private Long noofparticipantsMaleS2S;
    @JsonProperty("NoofparticipantsNJPC")
    private Long noofparticipantsNJPC;
    @JsonProperty("NoofparticipantsS2S")
    private Long noofparticipantsS2S;
    @JsonProperty("NoofWorkingDays")
    private Long noofWorkingDays;
    @JsonProperty("Nos")
    private Long nos;
    @JsonProperty("ObjectiveofDFWwork")
    private String objectiveofDFWwork;
    @JsonProperty("OtherObjective")
    private String otherObjective;
    @JsonProperty("OtherSubType")
    private String otherSubType;
    @JsonProperty("SchoolAanganwadiLearningCenterName")
    private String schoolAanganwadiLearningCenterName;
    @JsonProperty("State")
    private String state;
    @JsonProperty("TargetCommunity")
    private String targetCommunity;
    @JsonProperty("TypeofInitiative")
    private String typeofInitiative;
    @JsonProperty("CreatedBy")
    private String createdBy;
    @JsonProperty("ModifiedBy")
    private String modifiedBy;

    /**
     * No args constructor for use in serialization
     * 
     */
    public ActivityDTO() {
    }

    /**
     * 
     * @param sourceId
     * @param breadth
     * @param noofparticipantsMaleS2S
     * @param noofparticipantsFemaleNJPC
     * @param distribution
     * @param schoolAanganwadiLearningCenterName
     * @param localityVillageName
     * @param noofparticipantsFemaleS2S
     * @param noofdaysofParticipationNJPC
     * @param noofWorkingDays
     * @param noofparticipantsS2S
     * @param nos
     * @param typeofInitiative
     * @param diameter
     * @param activityEndDate
     * @param measurementType
     * @param targetCommunity
     * @param block
     * @param state
     * @param noofparticipantsNJPC
     * @param activityStartDate
     * @param activityCategory
     * @param depthHeight
     * @param length
     * @param demand
     * @param noofparticipantsFemaleDFW
     * @param objectiveofDFWwork
     * @param district
     * @param noofparticipantsMaleDFW
     * @param activityConductedWithStudents
     * @param noofparticipantsMaleNJPC
     * @param activitySubType
     * @param activityType
     * @param noofdaysofParticipationS2S
     * @param otherObjective
     * @param otherSubType
     * @param createdBy
     * @param modifiedBy
     */
    public ActivityDTO(String sourceId, String activityCategory, String activityConductedWithStudents, String activityEndDate, String activityStartDate, String activitySubType, String activityType, String block, Long breadth, Long depthHeight, Long diameter, String demand, String distribution, String district, Long length, String localityVillageName, String measurementType, Long noofdaysofParticipationNJPC, Long noofdaysofParticipationS2S, Long noofparticipantsFemaleDFW, Long noofparticipantsFemaleNJPC, Long noofparticipantsFemaleS2S, Long noofparticipantsMaleDFW, Long noofparticipantsMaleNJPC, Long noofparticipantsMaleS2S, Long noofparticipantsNJPC, Long noofparticipantsS2S, Long noofWorkingDays, Long nos, String objectiveofDFWwork, String schoolAanganwadiLearningCenterName, String state, String targetCommunity, String typeofInitiative,
                       String otherObjective, String otherSubType, String createdBy, String modifiedBy) {
        super();
        this.sourceId = sourceId;
        this.activityCategory = activityCategory;
        this.activityConductedWithStudents = activityConductedWithStudents;
        this.activityEndDate = activityEndDate;
        this.activityStartDate = activityStartDate;
        this.activitySubType = activitySubType;
        this.activityType = activityType;
        this.block = block;
        this.breadth = breadth;
        this.depthHeight = depthHeight;
        this.diameter = diameter;
        this.demand = demand;
        this.distribution = distribution;
        this.district = district;
        this.length = length;
        this.localityVillageName = localityVillageName;
        this.measurementType = measurementType;
        this.noofdaysofParticipationNJPC = noofdaysofParticipationNJPC;
        this.noofdaysofParticipationS2S = noofdaysofParticipationS2S;
        this.noofparticipantsFemaleDFW = noofparticipantsFemaleDFW;
        this.noofparticipantsFemaleNJPC = noofparticipantsFemaleNJPC;
        this.noofparticipantsFemaleS2S = noofparticipantsFemaleS2S;
        this.noofparticipantsMaleDFW = noofparticipantsMaleDFW;
        this.noofparticipantsMaleNJPC = noofparticipantsMaleNJPC;
        this.noofparticipantsMaleS2S = noofparticipantsMaleS2S;
        this.noofparticipantsNJPC = noofparticipantsNJPC;
        this.noofparticipantsS2S = noofparticipantsS2S;
        this.noofWorkingDays = noofWorkingDays;
        this.nos = nos;
        this.objectiveofDFWwork = objectiveofDFWwork;
        this.otherObjective = otherObjective;
        this.otherSubType = otherSubType;
        this.schoolAanganwadiLearningCenterName = schoolAanganwadiLearningCenterName;
        this.state = state;
        this.targetCommunity = targetCommunity;
        this.typeofInitiative = typeofInitiative;
        this.createdBy = createdBy;
        this.modifiedBy = modifiedBy;
    }

    @JsonProperty("SourceId")
    public String getSourceId() {
        return sourceId;
    }

    @JsonProperty("SourceId")
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    @JsonProperty("ActivityCategory")
    public String getActivityCategory() {
        return activityCategory;
    }

    @JsonProperty("ActivityCategory")
    public void setActivityCategory(String activityCategory) {
        this.activityCategory = activityCategory;
    }

    @JsonProperty("ActivityConductedWithStudents")
    public String getActivityConductedWithStudents() {
        return activityConductedWithStudents;
    }

    @JsonProperty("ActivityConductedWithStudents")
    public void setActivityConductedWithStudents(String activityConductedWithStudents) {
        this.activityConductedWithStudents = activityConductedWithStudents;
    }

    @JsonProperty("ActivityEndDate")
    public String getActivityEndDate() {
        return activityEndDate;
    }

    @JsonProperty("ActivityEndDate")
    public void setActivityEndDate(String activityEndDate) {
        this.activityEndDate = activityEndDate;
    }

    @JsonProperty("ActivityStartDate")
    public String getActivityStartDate() {
        return activityStartDate;
    }

    @JsonProperty("ActivityStartDate")
    public void setActivityStartDate(String activityStartDate) {
        this.activityStartDate = activityStartDate;
    }

    @JsonProperty("ActivitySubType")
    public String getActivitySubType() {
        return activitySubType;
    }

    @JsonProperty("ActivitySubType")
    public void setActivitySubType(String activitySubType) {
        this.activitySubType = activitySubType;
    }

    @JsonProperty("ActivityType")
    public String getActivityType() {
        return activityType;
    }

    @JsonProperty("ActivityType")
    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    @JsonProperty("Block")
    public String getBlock() {
        return block;
    }

    @JsonProperty("Block")
    public void setBlock(String block) {
        this.block = block;
    }

    @JsonProperty("Breadth")
    public Long getBreadth() {
        return breadth;
    }

    @JsonProperty("Breadth")
    public void setBreadth(Long breadth) {
        this.breadth = breadth;
    }

    @JsonProperty("DepthHeight")
    public Long getDepthHeight() {
        return depthHeight;
    }

    @JsonProperty("DepthHeight")
    public void setDepthHeight(Long depthHeight) {
        this.depthHeight = depthHeight;
    }

    @JsonProperty("Diameter")
    public Long getDiameter() {
        return diameter;
    }

    @JsonProperty("Diameter")
    public void setDiameter(Long diameter) {
        this.diameter = diameter;
    }

    @JsonProperty("Demand")
    public String getDemand() {
        return demand;
    }

    @JsonProperty("Demand")
    public void setDemand(String demand) {
        this.demand = demand;
    }

    @JsonProperty("DistributionSourceId")
    public String getDistributionSourceId() {
        return distribution;
    }

    @JsonProperty("DistributionSourceId")
    public void setDistributionSourceId(String distribution) {
        this.distribution = distribution;
    }

    @JsonProperty("District")
    public String getDistrict() {
        return district;
    }

    @JsonProperty("District")
    public void setDistrict(String district) {
        this.district = district;
    }

    @JsonProperty("Length")
    public Long getLength() {
        return length;
    }

    @JsonProperty("Length")
    public void setLength(Long length) {
        this.length = length;
    }

    @JsonProperty("LocalityVillageName")
    public String getLocalityVillageName() {
        return localityVillageName;
    }

    @JsonProperty("LocalityVillageName")
    public void setLocalityVillageName(String localityVillageName) {
        this.localityVillageName = localityVillageName;
    }

    @JsonProperty("MeasurementType")
    public String getMeasurementType() {
        return measurementType;
    }

    @JsonProperty("MeasurementType")
    public void setMeasurementType(String measurementType) {
        this.measurementType = measurementType;
    }

    @JsonProperty("NoofdaysofParticipationNJPC")
    public Long getNoofdaysofParticipationNJPC() {
        return noofdaysofParticipationNJPC;
    }

    @JsonProperty("NoofdaysofParticipationNJPC")
    public void setNoofdaysofParticipationNJPC(Long noofdaysofParticipationNJPC) {
        this.noofdaysofParticipationNJPC = noofdaysofParticipationNJPC;
    }

    @JsonProperty("NoofdaysofParticipationS2S")
    public Long getNoofdaysofParticipationS2S() {
        return noofdaysofParticipationS2S;
    }

    @JsonProperty("NoofdaysofParticipationS2S")
    public void setNoofdaysofParticipationS2S(Long noofdaysofParticipationS2S) {
        this.noofdaysofParticipationS2S = noofdaysofParticipationS2S;
    }

    @JsonProperty("NoofparticipantsFemaleDFW")
    public Long getNoofparticipantsFemaleDFW() {
        return noofparticipantsFemaleDFW;
    }

    @JsonProperty("NoofparticipantsFemaleDFW")
    public void setNoofparticipantsFemaleDFW(Long noofparticipantsFemaleDFW) {
        this.noofparticipantsFemaleDFW = noofparticipantsFemaleDFW;
    }

    @JsonProperty("NoofparticipantsFemaleNJPC")
    public Long getNoofparticipantsFemaleNJPC() {
        return noofparticipantsFemaleNJPC;
    }

    @JsonProperty("NoofparticipantsFemaleNJPC")
    public void setNoofparticipantsFemaleNJPC(Long noofparticipantsFemaleNJPC) {
        this.noofparticipantsFemaleNJPC = noofparticipantsFemaleNJPC;
    }

    @JsonProperty("NoofparticipantsFemaleS2S")
    public Long getNoofparticipantsFemaleS2S() {
        return noofparticipantsFemaleS2S;
    }

    @JsonProperty("NoofparticipantsFemaleS2S")
    public void setNoofparticipantsFemaleS2S(Long noofparticipantsFemaleS2S) {
        this.noofparticipantsFemaleS2S = noofparticipantsFemaleS2S;
    }

    @JsonProperty("NoofparticipantsMaleDFW")
    public Long getNoofparticipantsMaleDFW() {
        return noofparticipantsMaleDFW;
    }

    @JsonProperty("NoofparticipantsMaleDFW")
    public void setNoofparticipantsMaleDFW(Long noofparticipantsMaleDFW) {
        this.noofparticipantsMaleDFW = noofparticipantsMaleDFW;
    }

    @JsonProperty("NoofparticipantsMaleNJPC")
    public Long getNoofparticipantsMaleNJPC() {
        return noofparticipantsMaleNJPC;
    }

    @JsonProperty("NoofparticipantsMaleNJPC")
    public void setNoofparticipantsMaleNJPC(Long noofparticipantsMaleNJPC) {
        this.noofparticipantsMaleNJPC = noofparticipantsMaleNJPC;
    }

    @JsonProperty("NoofparticipantsMaleS2S")
    public Long getNoofparticipantsMaleS2S() {
        return noofparticipantsMaleS2S;
    }

    @JsonProperty("NoofparticipantsMaleS2S")
    public void setNoofparticipantsMaleS2S(Long noofparticipantsMaleS2S) {
        this.noofparticipantsMaleS2S = noofparticipantsMaleS2S;
    }

    @JsonProperty("NoofparticipantsNJPC")
    public Long getNoofparticipantsNJPC() {
        return noofparticipantsNJPC;
    }

    @JsonProperty("NoofparticipantsNJPC")
    public void setNoofparticipantsNJPC(Long noofparticipantsNJPC) {
        this.noofparticipantsNJPC = noofparticipantsNJPC;
    }

    @JsonProperty("NoofparticipantsS2S")
    public Long getNoofparticipantsS2S() {
        return noofparticipantsS2S;
    }

    @JsonProperty("NoofparticipantsS2S")
    public void setNoofparticipantsS2S(Long noofparticipantsS2S) {
        this.noofparticipantsS2S = noofparticipantsS2S;
    }

    @JsonProperty("NoofWorkingDays")
    public Long getNoofWorkingDays() {
        return noofWorkingDays;
    }

    @JsonProperty("NoofWorkingDays")
    public void setNoofWorkingDays(Long noofWorkingDays) {
        this.noofWorkingDays = noofWorkingDays;
    }

    @JsonProperty("Nos")
    public Long getNos() {
        return nos;
    }

    @JsonProperty("Nos")
    public void setNos(Long nos) {
        this.nos = nos;
    }

    @JsonProperty("ObjectiveofDFWwork")
    public String getObjectiveofDFWwork() {
        return objectiveofDFWwork;
    }

    @JsonProperty("ObjectiveofDFWwork")
    public void setObjectiveofDFWwork(String objectiveofDFWwork) {
        this.objectiveofDFWwork = objectiveofDFWwork;
    }

    @JsonProperty("OtherObjective")
    public String getOtherObjective() {
        return otherObjective;
    }

    @JsonProperty("OtherObjective")
    public void setOtherObjective(String otherObjective) {
        this.otherObjective = otherObjective;
    }

    @JsonProperty("OtherSubType")
    public String getOtherSubType() {
        return otherSubType;
    }

    @JsonProperty("OtherSubType")
    public void setOtherSubType(String otherSubType) {
        this.otherSubType = otherSubType;
    }

    @JsonProperty("SchoolAanganwadiLearningCenterName")
    public String getSchoolAanganwadiLearningCenterName() {
        return schoolAanganwadiLearningCenterName;
    }

    @JsonProperty("SchoolAanganwadiLearningCenterName")
    public void setSchoolAanganwadiLearningCenterName(String schoolAanganwadiLearningCenterName) {
        this.schoolAanganwadiLearningCenterName = schoolAanganwadiLearningCenterName;
    }

    @JsonProperty("State")
    public String getState() {
        return state;
    }

    @JsonProperty("State")
    public void setState(String state) {
        this.state = state;
    }

    @JsonProperty("TargetCommunity")
    public String getTargetCommunity() {
        return targetCommunity;
    }

    @JsonProperty("TargetCommunity")
    public void setTargetCommunity(String targetCommunity) {
        this.targetCommunity = targetCommunity;
    }

    @JsonProperty("TypeofInitiative")
    public String getTypeofInitiative() {
        return typeofInitiative;
    }

    @JsonProperty("TypeofInitiative")
    public void setTypeofInitiative(String typeofInitiative) {
        this.typeofInitiative = typeofInitiative;
    }

    @JsonProperty("CreatedBy")
    public String getCreatedBy() {
        return createdBy;
    }
    @JsonProperty("CreatedBy")
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    @JsonProperty("ModifiedBy")
    public String getModifiedBy() {
        return modifiedBy;
    }
    @JsonProperty("ModifiedBy")
    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }
}
