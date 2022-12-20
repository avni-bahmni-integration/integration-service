package org.avni_integration_service.goonj.worker.goonj;

import org.avni_integration_service.goonj.repository.GoonjBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DemandWorker extends BaseGoonjWorker {
    @Autowired
    public DemandWorker(@Qualifier("DemandRepository") GoonjBaseRepository crudRepository,
                        DemandEventWorker demandEventWorker) {
        super(crudRepository, demandEventWorker);
    }

    @Override
    public void process() throws Exception {
        HashMap<String, Object>[] demands = fetchEvents();
        for (Map<String, Object> demand : demands) {
            eventWorker.process(demand);
        }
    }

    @Override
    public void processDeletions() {
        List<String> deletedDemands = fetchDeletionEvents();
        for (String deletedDemand : deletedDemands) {
            eventWorker.processDeletion(deletedDemand);
        }
    }
}
