package org.avni_integration_service.goonj.worker.goonj;

import org.avni_integration_service.goonj.util.DateTimeUtil;
import org.avni_integration_service.integration_data.domain.Constants;
import org.avni_integration_service.integration_data.domain.IntegratingEntityStatus;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;

import java.util.Map;

public abstract class GoonjEventWorker {

    private final IntegratingEntityStatusRepository integratingEntityStatusRepository;

    public GoonjEventWorker(IntegratingEntityStatusRepository integratingEntityStatusRepository) {
        this.integratingEntityStatusRepository = integratingEntityStatusRepository;
    }

    abstract void process(Map<String, Object> event);
    abstract void processError(String uuid);
    abstract void cacheRunImmutables(Constants constants);

    <T> void updateLastDateTime(String entityType, Map<String, Object> event) {
        IntegratingEntityStatus intEnt = integratingEntityStatusRepository.findByEntityType(entityType);
        intEnt.setReadUptoDateTime(DateTimeUtil.convertToDate((String) event.get("LastUpdatedDateTime")));
        integratingEntityStatusRepository.save(intEnt);
    }
}
