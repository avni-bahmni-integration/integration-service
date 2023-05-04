package org.avni_integration_service.goonj.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;


@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "SourceId",
        "State",
        "District",
        "Block",
        "LocalityVillageName",
        "CreatedBy",
        "ModifiedBy",
        "nameOfAccount",
        "TypeofInitiative",
        "ActivityStartDate",
        "ActivityEndDate",
        "ActivityType",
        "ActivitySubType",
        "OtherSubType",
        "ActivityCategory",
        "NoofWorkingDays",
        "NoofparticipantsMaleCFW",
        "NoofparticipantsFemaleCFW",
        "NoofparticipantsMaleNJPC",
        "NoofparticipantsFemaleNJPC",
        "NoofparticipantsNJPCOther",
        "NoofparticipantsS2S",
        "NoofdaysofParticipationS2S",
        "NoofdaysofParticipationNJPC",
        "ActivityConductedWithStudents",
        "TypeOfSchool",
        "S2SPhotograph",
        "NJPCPhotograph",
        "ObjectiveofCFWwork",
        "OtherObjective",
        "SchoolAanganwadiLearningCenterName",
        "MeasurementType",
        "DepthHeight",
        "Diameter",
        "Length",
        "Breadth",
        "Nos",
        "BeforeImplementationPhotograph",
        "DuringImplementationPhotograph",
        "AfterImplementationPhotograph"
})
public class ActivityDTO {

    @JsonProperty("SourceId")
    private String sourceId;
    @JsonProperty("nameOfAccount")
    private String nameOfAccount;
    @JsonProperty("ActivityCategory")
    private String activityCategory;
    @JsonProperty("ActivityConductedWithStudents")
    private String activityConductedWithStudents;
    @JsonProperty("TypeOfSchool")
    private String typeOfSchool;
    @JsonProperty("S2SPhotograph")
    private String s2sPhotograph;
    @JsonProperty("NJPCPhotograph")
    private String njpcPhotograph;
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
    @JsonProperty("NoofparticipantsFemaleCFW")
    private int noofparticipantsFemaleCFW;
    @JsonProperty("NoofparticipantsFemaleNJPC")
    private int noofparticipantsFemaleNJPC;
    @JsonProperty("NoofparticipantsMaleCFW")
    private int noofparticipantsMaleCFW;
    @JsonProperty("NoofparticipantsMaleNJPC")
    private int noofparticipantsMaleNJPC;
    @JsonProperty("NoofparticipantsNJPCOther")
    private int noofparticipantsNJPCOther;
    @JsonProperty("NoofparticipantsS2S")
    private Long noofparticipantsS2S;
    @JsonProperty("NoofWorkingDays")
    private Long noofWorkingDays;
    @JsonProperty("Nos")
    private Integer nos;
    @JsonProperty("ObjectiveofCFWwork")
    private String objectiveofCFWwork;
    @JsonProperty("OtherObjective")
    private String otherObjective;
    @JsonProperty("OtherSubType")
    private String otherSubType;
    @JsonProperty("SchoolAanganwadiLearningCenterName")
    private String schoolAanganwadiLearningCenterName;
    @JsonProperty("State")
    private String state;
    @JsonProperty("TypeofInitiative")
    private String typeofInitiative;
    @JsonProperty("CreatedBy")
    private String createdBy;
    @JsonProperty("ModifiedBy")
    private String modifiedBy;
    @JsonProperty("BeforeImplementationPhotograph")
    private String beforeImplementationPhotograph;
    @JsonProperty("DuringImplementationPhotograph")
    private String duringImplementationPhotograph;
    @JsonProperty("AfterImplementationPhotograph")
    private String afterImplementationPhotograph;

    /**
     * No args constructor for use in serialization
     */
    public ActivityDTO() {
    }

    /**
     * @param sourceId
     * @param nameOfAccount
     * @param breadth
     * @param noofparticipantsFemaleNJPC
     * @param schoolAanganwadiLearningCenterName
     * @param localityVillageName
     * @param noofdaysofParticipationNJPC
     * @param noofWorkingDays
     * @param noofparticipantsS2S
     * @param nos
     * @param typeofInitiative
     * @param diameter
     * @param activityEndDate
     * @param measurementType
     * @param block
     * @param state
     * @param activityStartDate
     * @param activityCategory
     * @param depthHeight
     * @param length
     * @param noofparticipantsFemaleCFW
     * @param objectiveofCFWwork
     * @param district
     * @param noofparticipantsMaleCFW
     * @param activityConductedWithStudents
     * @param typeOfSchool
     * @param s2sPhotograph
     * @param njpcPhotograph
     * @param noofparticipantsMaleNJPC
     * @param noofparticipantsNJPCOther
     * @param activitySubType
     * @param activityType
     * @param noofdaysofParticipationS2S
     * @param otherObjective
     * @param otherSubType
     * @param createdBy
     * @param modifiedBy
     * @param beforeImplementationPhotograph
     * @param duringImplementationPhotograph
     * @param afterImplementationPhotograph
     */
    public ActivityDTO(String sourceId, String nameOfAccount, String activityCategory, String activityConductedWithStudents,
                       String typeOfSchool, String s2sPhotograph, String njpcPhotograph, String activityEndDate,
                       String activityStartDate, String activitySubType, String activityType, String block,
                       Long breadth, Long depthHeight, Long diameter, String district, Long length,
                       String localityVillageName, String measurementType, Long noofdaysofParticipationNJPC,
                       Long noofdaysofParticipationS2S, int noofparticipantsFemaleCFW, int noofparticipantsFemaleNJPC,
                       int noofparticipantsMaleCFW, int noofparticipantsMaleNJPC, int noofparticipantsNJPCOther, Long noofparticipantsS2S,
                       Long noofWorkingDays, Integer nos, String objectiveofCFWwork, String schoolAanganwadiLearningCenterName,
                       String state, String typeofInitiative, String otherObjective, String otherSubType,
                       String createdBy, String modifiedBy, String beforeImplementationPhotograph, String duringImplementationPhotograph,
                       String afterImplementationPhotograph) {
        super();
        this.sourceId = sourceId;
        this.nameOfAccount = nameOfAccount;
        this.activityCategory = activityCategory;
        this.activityConductedWithStudents = activityConductedWithStudents;
        this.typeOfSchool = typeOfSchool;
        this.s2sPhotograph = s2sPhotograph;
        this.njpcPhotograph = njpcPhotograph;
        this.activityEndDate = activityEndDate;
        this.activityStartDate = activityStartDate;
        this.activitySubType = activitySubType;
        this.activityType = activityType;
        this.block = block;
        this.breadth = breadth;
        this.depthHeight = depthHeight;
        this.diameter = diameter;
        this.district = district;
        this.length = length;
        this.localityVillageName = localityVillageName;
        this.measurementType = measurementType;
        this.noofdaysofParticipationNJPC = noofdaysofParticipationNJPC;
        this.noofdaysofParticipationS2S = noofdaysofParticipationS2S;
        this.noofparticipantsFemaleCFW = noofparticipantsFemaleCFW;
        this.noofparticipantsFemaleNJPC = noofparticipantsFemaleNJPC;
        this.noofparticipantsMaleCFW = noofparticipantsMaleCFW;
        this.noofparticipantsMaleNJPC = noofparticipantsMaleNJPC;
        this.noofparticipantsNJPCOther = noofparticipantsNJPCOther;
        this.noofparticipantsS2S = noofparticipantsS2S;
        this.noofWorkingDays = noofWorkingDays;
        this.nos = nos;
        this.objectiveofCFWwork = objectiveofCFWwork;
        this.otherObjective = otherObjective;
        this.otherSubType = otherSubType;
        this.schoolAanganwadiLearningCenterName = schoolAanganwadiLearningCenterName;
        this.state = state;
        this.typeofInitiative = typeofInitiative;
        this.createdBy = createdBy;
        this.modifiedBy = modifiedBy;
        this.beforeImplementationPhotograph = beforeImplementationPhotograph;
        this.duringImplementationPhotograph = duringImplementationPhotograph;
        this.afterImplementationPhotograph = afterImplementationPhotograph;
    }

    @JsonProperty("SourceId")
    public String getSourceId() {
        return sourceId;
    }

    @JsonProperty("SourceId")
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    @JsonProperty("nameOfAccount")
    public String getnameOfAccount() {
        return nameOfAccount;
    }

    @JsonProperty("nameOfAccount")
    public void setnameOfAccount(String accountName) {
        nameOfAccount = accountName;
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

    @JsonProperty("TypeOfSchool")
    public String getTypeOfSchool() {
        return typeOfSchool;
    }

    @JsonProperty("TypeOfSchool")
    public void setTypeOfSchool(String typeOfSchool) {
        this.typeOfSchool = typeOfSchool;
    }

    @JsonProperty("S2SPhotograph")
    public String getS2sPhotograph() {
        return s2sPhotograph;
    }

    @JsonProperty("S2SPhotograph")
    public void setS2sPhotograph(String s2sPhotograph) {
        this.s2sPhotograph = s2sPhotograph;
    }

    @JsonProperty("NJPCPhotograph")
    public String getNjpcPhotograph() {
        return njpcPhotograph;
    }

    @JsonProperty("NJPCPhotograph")
    public void setNjpcPhotograph(String njpcPhotograph) {
        this.njpcPhotograph = njpcPhotograph;
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

    @JsonProperty("NoofparticipantsFemaleCFW")
    public int getNoofparticipantsFemaleCFW() {
        return noofparticipantsFemaleCFW;
    }

    @JsonProperty("NoofparticipantsFemaleCFW")
    public void setNoofparticipantsFemaleCFW(int noofparticipantsFemaleCFW) {
        this.noofparticipantsFemaleCFW = noofparticipantsFemaleCFW;
    }

    @JsonProperty("NoofparticipantsFemaleNJPC")
    public int getNoofparticipantsFemaleNJPC() {
        return noofparticipantsFemaleNJPC;
    }

    @JsonProperty("NoofparticipantsFemaleNJPC")
    public void setNoofparticipantsFemaleNJPC(int noofparticipantsFemaleNJPC) {
        this.noofparticipantsFemaleNJPC = noofparticipantsFemaleNJPC;
    }

    @JsonProperty("NoofparticipantsMaleCFW")
    public int getNoofparticipantsMaleCFW() {
        return noofparticipantsMaleCFW;
    }

    @JsonProperty("NoofparticipantsMaleCFW")
    public void setNoofparticipantsMaleCFW(Integer noofparticipantsMaleCFW) {
        this.noofparticipantsMaleCFW = noofparticipantsMaleCFW;
    }

    @JsonProperty("NoofparticipantsMaleNJPC")
    public int getNoofparticipantsMaleNJPC() {
        return noofparticipantsMaleNJPC;
    }

    @JsonProperty("NoofparticipantsMaleNJPC")
    public void setNoofparticipantsMaleNJPC(int noofparticipantsMaleNJPC) {
        this.noofparticipantsMaleNJPC = noofparticipantsMaleNJPC;
    }
    @JsonProperty("NoofparticipantsNJPCOther")
    public int getNoofparticipantsNJPCOther() {
        return noofparticipantsNJPCOther;
    }
    @JsonProperty("NoofparticipantsNJPCOther")
    public void setNoofparticipantsNJPCOther(int noofparticipantsNJPCOther) {
        this.noofparticipantsNJPCOther = noofparticipantsNJPCOther;
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
    public Integer getNos() {
        return nos;
    }

    @JsonProperty("Nos")
    public void setNos(Integer nos) {
        this.nos = nos;
    }

    @JsonProperty("ObjectiveofCFWwork")
    public String getObjectiveofCFWwork() {
        return objectiveofCFWwork;
    }

    @JsonProperty("ObjectiveofCFWwork")
    public void setObjectiveofCFWwork(String objectiveofCFWwork) {
        this.objectiveofCFWwork = objectiveofCFWwork;
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
    @JsonProperty("BeforeImplementationPhotograph")
    public String getBeforeImplementationPhotograph() {
        return beforeImplementationPhotograph;
    }
    @JsonProperty("BeforeImplementationPhotograph")
    public void setBeforeImplementationPhotograph(String beforeImplementationPhotograph) {
        this.beforeImplementationPhotograph = beforeImplementationPhotograph;
    }
    @JsonProperty("DuringImplementationPhotograph")
    public String getDuringImplementationPhotograph() {
        return duringImplementationPhotograph;
    }
    @JsonProperty("DuringImplementationPhotograph")
    public void setDuringImplementationPhotograph(String duringImplementationPhotograph) {
        this.duringImplementationPhotograph = duringImplementationPhotograph;
    }
    @JsonProperty("AfterImplementationPhotograph")
    public String getAfterImplementationPhotograph() {
        return afterImplementationPhotograph;
    }
    @JsonProperty("AfterImplementationPhotograph")
    public void setAfterImplementationPhotograph(String afterImplementationPhotograph) {
        this.afterImplementationPhotograph = afterImplementationPhotograph;
    }
}
