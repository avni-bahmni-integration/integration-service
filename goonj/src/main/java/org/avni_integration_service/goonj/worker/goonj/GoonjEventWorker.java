package org.avni_integration_service.goonj.worker.goonj;

import org.apache.log4j.Logger;
import org.avni_integration_service.goonj.GoonjEntityType;
import org.avni_integration_service.goonj.GoonjErrorType;
import org.avni_integration_service.goonj.service.AvniGoonjErrorService;
import org.avni_integration_service.goonj.util.DateTimeUtil;
import org.avni_integration_service.integration_data.domain.IntegratingEntityStatus;
import org.avni_integration_service.integration_data.domain.IntegrationSystem;
import org.avni_integration_service.integration_data.domain.error.ErrorType;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.avni_integration_service.integration_data.service.error.ErrorClassifier;

import java.util.Date;
import java.util.Map;

public abstract class GoonjEventWorker {

    private static final Logger logger = Logger.getLogger(GoonjEventWorker.class);
    private final AvniGoonjErrorService avniGoonjErrorService;
    private final IntegratingEntityStatusRepository integratingEntityStatusRepository;
    protected final GoonjEntityType entityType;
    private final ErrorClassifier errorClassifier;
    private final IntegrationSystem integrationSystem;

    public GoonjEventWorker(AvniGoonjErrorService avniGoonjErrorService, IntegratingEntityStatusRepository integratingEntityStatusRepository,
                            GoonjEntityType entityType, ErrorClassifier errorClassifier, IntegrationSystem integrationSystem) {
        this.avniGoonjErrorService = avniGoonjErrorService;
        this.integratingEntityStatusRepository = integratingEntityStatusRepository;
        this.entityType = entityType;
        this.errorClassifier = errorClassifier;
        this.integrationSystem = integrationSystem;
    }

    abstract void process(Map<String, Object> event) throws Exception;

    void updateErrorRecordAndSyncStatus(Map<String, Object> callResponse, boolean updateSyncStatus, String sid) {
        avniGoonjErrorService.successfullyProcessed(sid, entityType);
        updateSyncStatus(callResponse, updateSyncStatus);
    }

    void createOrUpdateErrorRecordAndSyncStatus(Map<String, Object> callResponse, boolean updateSyncStatus, String sid,
                                                GoonjErrorType goonjErrorType, String errorMsg) {
        avniGoonjErrorService.errorOccurred(sid, goonjErrorType, entityType, errorMsg);
        updateSyncStatus(callResponse, updateSyncStatus);
    }

    void updateSyncStatus(Map<String, Object> callResponse, boolean updateSyncStatus) {
        if (updateSyncStatus) {
            updateReadUptoDateTime(callResponse);
        }
    }

    <T> void updateReadUptoDateTime(Map<String, Object> event) {
        IntegratingEntityStatus intEnt = integratingEntityStatusRepository.findByEntityType(entityType.name());
        intEnt.setReadUptoDateTime(DateTimeUtil.convertToDate((String) event.get("LastUpdatedDateTime")));
        integratingEntityStatusRepository.save(intEnt);
    }

    <T> void updateReadUptoDateTime(Date deletedDateTime) {
        IntegratingEntityStatus intEnt = integratingEntityStatusRepository.findByEntityType(entityType.name());
        intEnt.setReadUptoDateTime(deletedDateTime);
        integratingEntityStatusRepository.save(intEnt);
    }

    protected void handleError(Map<String, Object> event, Exception exception, String entityId, GoonjErrorType goonjErrorType) throws Exception {
        logger.error(String.format("Goonj %s %s could not be synced to Goonj Salesforce. ", entityType, event.get(entityId)), exception);
        ErrorType classifiedErrorType = errorClassifier.classify(integrationSystem, exception);
        if(classifiedErrorType == null) {
            throw exception;
        }
        createOrUpdateErrorRecordAndSyncStatus(event, true, (String) event.get(entityId),
                goonjErrorType, exception.getLocalizedMessage());
    }

    public abstract void processDeletion(String deletedEntity);
}
