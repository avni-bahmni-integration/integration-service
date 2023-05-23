package org.avni_integration_service.goonj.worker.goonj;

import org.avni_integration_service.goonj.repository.GoonjBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InventoryWorker extends BaseGoonjWorker {
    @Autowired
    public InventoryWorker(@Qualifier("InventoryRepository") GoonjBaseRepository crudRepository,
                           InventoryEventWorker inventoryEventWorker) {
        super(crudRepository, inventoryEventWorker);
    }

    @Override
    public void process() throws Exception {
        HashMap<String, Object>[] inventoryItems = fetchEvents();
        for (Map<String, Object> items : inventoryItems) {
            eventWorker.process(items);
        }
    }

    @Override
    public void processDeletions() {
        List<String> deletedItems = fetchDeletionEvents();
        for (String deletedDS : deletedItems) {
            eventWorker.processDeletion(deletedDS);
        }
    }
}
