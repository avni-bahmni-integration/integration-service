package org.avni_integration_service.goonj.worker.goonj;

import org.avni_integration_service.integration_data.domain.Constants;

import java.util.Map;

public interface IGoonjEventWorker {
    void process(Map<String, Object> event);
    void processError(String uuid);
    void cacheRunImmutables(Constants constants);
}
