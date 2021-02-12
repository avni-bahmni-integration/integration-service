package org.bahmni_avni_integration.domain;

import org.bahmni_avni_integration.BahmniEntityType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
public class ErrorRecord extends BaseEntity {
    @Column
    @Enumerated(EnumType.STRING)
    private AvniEntityType avniEntityType;

    @Column
    @Enumerated(EnumType.STRING)
    private BahmniEntityType bahmniEntityType;

    @Column
    private String subjectPatientExternalId;

    @Column
    private String enrolmentExternalId;

    @Column
    private String programEncounterExternalId;

    @Column
    private String encounterExternalId;

    @Column
    @Enumerated(EnumType.STRING)
    private ErrorType errorType;

    public AvniEntityType getAvniEntityType() {
        return avniEntityType;
    }

    public void setAvniEntityType(AvniEntityType avniEntityType) {
        this.avniEntityType = avniEntityType;
    }

    public BahmniEntityType getBahmniEntityType() {
        return bahmniEntityType;
    }

    public void setBahmniEntityType(BahmniEntityType bahmniEntityType) {
        this.bahmniEntityType = bahmniEntityType;
    }

    public String getSubjectPatientExternalId() {
        return subjectPatientExternalId;
    }

    public void setSubjectPatientExternalId(String subjectPatientExternalId) {
        this.subjectPatientExternalId = subjectPatientExternalId;
    }

    public String getEnrolmentExternalId() {
        return enrolmentExternalId;
    }

    public void setEnrolmentExternalId(String enrolmentExternalId) {
        this.enrolmentExternalId = enrolmentExternalId;
    }

    public String getProgramEncounterExternalId() {
        return programEncounterExternalId;
    }

    public void setProgramEncounterExternalId(String programEncounterExternalId) {
        this.programEncounterExternalId = programEncounterExternalId;
    }

    public String getEncounterExternalId() {
        return encounterExternalId;
    }

    public void setEncounterExternalId(String encounterExternalId) {
        this.encounterExternalId = encounterExternalId;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(ErrorType errorType) {
        this.errorType = errorType;
    }
}