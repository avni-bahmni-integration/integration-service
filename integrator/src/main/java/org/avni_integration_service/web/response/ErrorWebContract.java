package org.avni_integration_service.web.response;

import java.util.Date;

public class ErrorWebContract {
    private int id;
    private int errorType;
    private Date loggedAt;
    private String integratingEntityType;
    private String avniEntityType;
    private String entityId;
    private boolean processingDisabled;
    private String integrationSystem;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getErrorType() {
        return errorType;
    }

    public void setErrorType(int errorType) {
        this.errorType = errorType;
    }

    public Date getLoggedAt() {
        return loggedAt;
    }

    public void setLoggedAt(Date loggedAt) {
        this.loggedAt = loggedAt;
    }

    public String getIntegratingEntityType() {
        return integratingEntityType;
    }

    public void setIntegratingEntityType(String integratingEntityType) {
        this.integratingEntityType = integratingEntityType;
    }

    public String getAvniEntityType() {
        return avniEntityType;
    }

    public void setAvniEntityType(String avniEntityType) {
        this.avniEntityType = avniEntityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public boolean isProcessingDisabled() {
        return processingDisabled;
    }

    public void setProcessingDisabled(boolean processingDisabled) {
        this.processingDisabled = processingDisabled;
    }

    public String getIntegrationSystem() {
        return integrationSystem;
    }

    public void setIntegrationSystem(String integrationSystem) {
        this.integrationSystem = integrationSystem;
    }
}
