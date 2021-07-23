package org.bahmni_avni_integration.integration_data.domain;

import org.bahmni_avni_integration.integration_data.BahmniEntityType;

import javax.persistence.*;
import java.util.*;

@Entity
public class ErrorRecord extends BaseEntity {
    @Column
    @Enumerated(EnumType.STRING)
    private AvniEntityType avniEntityType;

    @Column
    @Enumerated(EnumType.STRING)
    private BahmniEntityType bahmniEntityType;

    @Column
    private String entityId;

    @Column
    private boolean processingDisabled;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "errorRecord")
    private Set<ErrorRecordLog> errorRecordLogs = new HashSet<>();

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

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public boolean hasThisAsLastErrorType(ErrorType errorType) {
        ErrorRecordLog errorRecordLog = this.errorRecordLogs.stream().sorted(Comparator.comparing(BaseEntity::getId)).reduce((first, second) -> second).orElse(null);
        return Objects.equals(errorRecordLog.getErrorType(), errorType);
    }

    public void addErrorType(ErrorType errorType) {
        ErrorRecordLog errorRecordLog = new ErrorRecordLog();
        errorRecordLog.setErrorType(errorType);
        errorRecordLog.setLoggedAt(new Date());
        errorRecordLogs.add(errorRecordLog);
        errorRecordLog.setErrorRecord(this);
    }

    public boolean isProcessingDisabled() {
        return processingDisabled;
    }

    public void setProcessingDisabled(boolean processingDisabled) {
        this.processingDisabled = processingDisabled;
    }
}
