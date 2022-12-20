package org.avni_integration_service.bahmni.contract;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMRSPatient {
    private String uuid;
    private String display;
    private OpenMRSPerson person;
    private List<OpenMRSPatientIdentifier> identifiers;
    private boolean voided;
    private OpenMRSAuditInfo auditInfo;

    public OpenMRSPatient(OpenMRSPerson person) {
        this.person = person;
    }

    public OpenMRSPatient() {
    }

    public OpenMRSPatient addIdentifier(OpenMRSPatientIdentifier identifier) {
        if (identifiers == null) identifiers = new ArrayList<OpenMRSPatientIdentifier>();

        identifiers.add(identifier);
        return this;
    }

    public OpenMRSPerson getPerson() {
        return person;
    }

    public void setPerson(OpenMRSPerson person) {
        this.person = person;
    }

    public List<OpenMRSPatientIdentifier> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<OpenMRSPatientIdentifier> identifiers) {
        this.identifiers = identifiers;
    }

    public String getName() {
        if (getPerson().getPreferredName() != null)
            return getPerson().getPreferredName().getDisplay();
        return "";
    }

    public String getLocalName() {
        return getPerson().getLocalName();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getPatientId() {
        OpenMRSPatientIdentifier openMRSPatientIdentifier = this.getIdentifiers().stream().filter(OpenMRSPatientIdentifier::isPreferred).findFirst().orElse(null);
        return openMRSPatientIdentifier == null ? "" : openMRSPatientIdentifier.getIdentifier();
    }

    public boolean isVoided() {
        return voided;
    }

    public void setVoided(boolean voided) {
        this.voided = voided;
    }

    public OpenMRSAuditInfo getAuditInfo() {
        return auditInfo;
    }

    public void setAuditInfo(OpenMRSAuditInfo auditInfo) {
        this.auditInfo = auditInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpenMRSPatient that = (OpenMRSPatient) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
