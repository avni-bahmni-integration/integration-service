package org.avni_integration_service.integration_data.domain.error;

import org.avni_integration_service.integration_data.domain.AvniEntityType;
import org.avni_integration_service.integration_data.domain.framework.BaseEntity;
import org.avni_integration_service.integration_data.domain.framework.BaseIntegrationSpecificEntity;

import javax.persistence.*;
import java.util.*;

@Entity
public class ErrorRecord extends BaseIntegrationSpecificEntity {
    private static final String EMPTY_STRING = "";
    @Column
    @Enumerated(EnumType.STRING)
    private AvniEntityType avniEntityType;

    @Column
    private String integratingEntityType;

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

    public String getIntegratingEntityType() {
        return integratingEntityType;
    }

    public void setIntegratingEntityType(String integratingEntityType) {
        this.integratingEntityType = integratingEntityType;
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
        addErrorType(errorType, EMPTY_STRING);
    }

    public void addErrorType(ErrorType errorType, String errorMsg) {
        ErrorRecordLog errorRecordLog = new ErrorRecordLog();
        errorRecordLog.setErrorType(errorType);
        errorRecordLog.setErrorMsg(errorMsg);
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
