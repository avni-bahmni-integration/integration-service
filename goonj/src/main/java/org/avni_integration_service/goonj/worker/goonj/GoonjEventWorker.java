package org.avni_integration_service.goonj.worker.goonj;

import org.avni_integration_service.goonj.util.DateTimeUtil;
import org.avni_integration_service.integration_data.domain.Constants;
import org.avni_integration_service.integration_data.domain.IntegratingEntityStatus;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;

import java.util.Date;
import java.util.Map;

public abstract class GoonjEventWorker {

    private final IntegratingEntityStatusRepository integratingEntityStatusRepository;

    private final String entityType;

    public GoonjEventWorker(IntegratingEntityStatusRepository integratingEntityStatusRepository, String entityType) {
        this.integratingEntityStatusRepository = integratingEntityStatusRepository;
        this.entityType = entityType;
    }

    abstract void process(Map<String, Object> event);
    abstract void processError(String uuid);

    <T> void updateReadUptoDateTime(Map<String, Object> event) {
        IntegratingEntityStatus intEnt = integratingEntityStatusRepository.findByEntityType(entityType);
        intEnt.setReadUptoDateTime(DateTimeUtil.convertToDate((String) event.get("LastUpdatedDateTime")));
        integratingEntityStatusRepository.save(intEnt);
    }

    <T> void updateReadUptoDateTime(Date deletedDateTime) {
        IntegratingEntityStatus intEnt = integratingEntityStatusRepository.findByEntityType(entityType);
        intEnt.setReadUptoDateTime(deletedDateTime);
        integratingEntityStatusRepository.save(intEnt);
    }


    public abstract void processDeletion(String deletedEntity);
}
