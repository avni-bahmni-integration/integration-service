package org.avni_integration_service.web.response;

import java.util.Date;

public class ErrorWebContract {
    private int id;
    private int errorType;
    private Date loggedAt;
    private String bahmniEntityType;
    private String avniEntityType;
    private String entityUuid;
    private boolean processingDisabled;

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

    public String getBahmniEntityType() {
        return bahmniEntityType;
    }

    public void setBahmniEntityType(String bahmniEntityType) {
        this.bahmniEntityType = bahmniEntityType;
    }

    public String getAvniEntityType() {
        return avniEntityType;
    }

    public void setAvniEntityType(String avniEntityType) {
        this.avniEntityType = avniEntityType;
    }

    public String getEntityUuid() {
        return entityUuid;
    }

    public void setEntityUuid(String entityUuid) {
        this.entityUuid = entityUuid;
    }

    public boolean isProcessingDisabled() {
        return processingDisabled;
    }

    public void setProcessingDisabled(boolean processingDisabled) {
        this.processingDisabled = processingDisabled;
    }
}
