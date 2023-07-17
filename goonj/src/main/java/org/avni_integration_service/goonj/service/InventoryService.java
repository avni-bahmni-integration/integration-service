package org.avni_integration_service.goonj.service;

import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.goonj.config.GoonjContextProvider;
import org.avni_integration_service.goonj.domain.Demand;
import org.avni_integration_service.goonj.domain.Inventory;
import org.avni_integration_service.goonj.repository.InventoryRepository;
import org.avni_integration_service.integration_data.repository.IntegrationSystemRepository;
import org.avni_integration_service.integration_data.repository.MappingMetaDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

import static org.avni_integration_service.goonj.config.GoonjMappingDbConstants.MappingGroup_Inventory;

@Service
public class InventoryService extends BaseGoonjService {
    private final InventoryRepository inventoryRepository;

    @Autowired
    public InventoryService(InventoryRepository inventoryRepositoryGoonj,
                            MappingMetaDataRepository mappingMetaDataRepository,
                            IntegrationSystemRepository integrationSystemRepository, GoonjContextProvider goonjContextProvider) {
        super(mappingMetaDataRepository, goonjContextProvider);
        this.inventoryRepository = inventoryRepositoryGoonj;
    }

    public HashMap<String, Object> getImplementationInventory(String uuid) {
        return inventoryRepository.getInventoryItemsDTO(uuid);
    }

    public void populateObservations(Subject subject , Inventory inventory) {
        populateObservations(subject, inventory, MappingGroup_Inventory);
    }
}
