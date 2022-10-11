package org.avni_integration_service.goonj.worker.goonj;

import org.avni_integration_service.goonj.GoonjEntityType;
import org.avni_integration_service.goonj.GoonjErrorType;
import org.avni_integration_service.goonj.service.AvniGoonjErrorService;
import org.avni_integration_service.goonj.util.DateTimeUtil;
import org.avni_integration_service.integration_data.domain.IntegratingEntityStatus;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;

import java.util.Date;
import java.util.Map;

public abstract class GoonjEventWorker {

    private final AvniGoonjErrorService avniGoonjErrorService;
    private final IntegratingEntityStatusRepository integratingEntityStatusRepository;

    private final GoonjEntityType entityType;

    public GoonjEventWorker(AvniGoonjErrorService avniGoonjErrorService, IntegratingEntityStatusRepository integratingEntityStatusRepository, GoonjEntityType entityType) {
        this.avniGoonjErrorService = avniGoonjErrorService;
        this.integratingEntityStatusRepository = integratingEntityStatusRepository;
        this.entityType = entityType;
    }

    abstract void process(Map<String, Object> event);

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


    public abstract void processDeletion(String deletedEntity);
}
