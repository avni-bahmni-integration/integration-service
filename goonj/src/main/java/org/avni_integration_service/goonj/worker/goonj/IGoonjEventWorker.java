package org.avni_integration_service.goonj.worker.goonj;

import org.avni_integration_service.integration_data.domain.Constants;

import java.util.Map;

public interface IGoonjEventWorker {

    public void process(Map<String, Object> event);
    public void processError(String uuid);
    public void cacheRunImmutables(Constants constants);
}
