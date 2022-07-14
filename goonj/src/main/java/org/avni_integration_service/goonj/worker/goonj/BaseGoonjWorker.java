package org.avni_integration_service.goonj.worker.goonj;

import org.apache.log4j.Logger;
import org.avni_integration_service.goonj.repository.GoonjBaseRepository;
import org.avni_integration_service.integration_data.domain.Constants;

import java.util.HashMap;

public abstract class BaseGoonjWorker {
    private static final Logger logger = Logger.getLogger(BaseGoonjWorker.class);

    private final GoonjBaseRepository crudRepository;
    protected final IGoonjEventWorker eventWorker;

    public BaseGoonjWorker(GoonjBaseRepository crudRepository, IGoonjEventWorker eventWorker) {
        this.crudRepository = crudRepository;
        this.eventWorker = eventWorker;
    }

    public abstract void process();

    protected HashMap<String, Object>[] fetchEvents() {
        HashMap<String, Object>[] events = crudRepository.fetchEvents();
        if(events == null) {
            return new HashMap[0];
        }
        return events;
    }

    public void cacheRunImmutables(Constants constants) {
        eventWorker.cacheRunImmutables(constants);
    }
}
