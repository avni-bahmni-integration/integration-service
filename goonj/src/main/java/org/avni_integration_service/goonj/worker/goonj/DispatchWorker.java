package org.avni_integration_service.goonj.worker.goonj;

import org.avni_integration_service.goonj.repository.GoonjBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DispatchWorker extends BaseGoonjWorker {
    @Autowired
    public DispatchWorker(@Qualifier("DispatchRepository") GoonjBaseRepository crudRepository,
                        DispatchEventWorker dispatchEventWorker) {
        super(crudRepository, dispatchEventWorker);
    }

    @Override
    public void process() {
        HashMap<String, Object>[] dispatches = fetchEvents();
        for (Map<String, Object> dispatch : dispatches) {
            eventWorker.process(dispatch);
        }
    }
}
