package org.avni_integration_service.goonj.worker.goonj;

import org.apache.log4j.Logger;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.avni.repository.AvniSubjectRepository;
import org.avni_integration_service.avni.worker.ErrorRecordWorker;
import org.avni_integration_service.goonj.GoonjEntityType;
import org.avni_integration_service.goonj.GoonjErrorType;
import org.avni_integration_service.goonj.config.GoonjContextProvider;
import org.avni_integration_service.goonj.domain.Inventory;
import org.avni_integration_service.goonj.service.AvniGoonjErrorService;
import org.avni_integration_service.goonj.service.InventoryService;
import org.avni_integration_service.goonj.util.DateTimeUtil;
import org.avni_integration_service.integration_data.domain.IntegratingEntityStatus;
import org.avni_integration_service.integration_data.domain.IntegrationSystem;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.avni_integration_service.integration_data.service.error.ErrorClassifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.Map;

@Component
public class InventoryEventWorker extends GoonjEventWorker implements ErrorRecordWorker {
    private static final Logger logger = Logger.getLogger(InventoryEventWorker.class);
    private final InventoryService inventoryService;
    private final AvniGoonjErrorService avniGoonjErrorService;
    private final AvniSubjectRepository avniSubjectRepository;
    private final IntegratingEntityStatusRepository integratingEntityStatusRepository;

    @Autowired
    public InventoryEventWorker(InventoryService inventoryService, AvniGoonjErrorService avniGoonjErrorService,
                                AvniSubjectRepository avniSubjectRepository, IntegratingEntityStatusRepository integratingEntityStatusRepository,
                                ErrorClassifier errorClassifier, GoonjContextProvider goonjContextProvider) {
        super(avniGoonjErrorService, integratingEntityStatusRepository, GoonjEntityType.Inventory, errorClassifier, goonjContextProvider);
        this.inventoryService = inventoryService;
        this.avniGoonjErrorService = avniGoonjErrorService;
        this.avniSubjectRepository = avniSubjectRepository;
        this.integratingEntityStatusRepository = integratingEntityStatusRepository;
    }

    public void process(Map<String, Object> event) throws Exception {
        try {
            processImplementationInventory(event);
            updateErrorRecordAndSyncStatus(event, true, (String) event.get("ImplementationInventoryId"));
        } catch (Exception e) {
            handleError(event, e, "ImplementationInventoryId", GoonjErrorType.ImplementationInventoryAttributesMismatch);
        }
    }

    private void processImplementationInventory(Map<String, Object> inventoryResponse) {
        logger.debug(String.format("Processing implementation inventory: name %s || uuid %s", inventoryResponse.get("ImplementationInventoryName"), inventoryResponse.get("ImplementationInventoryId")));
        Inventory inventoryItems = Inventory.from(inventoryResponse);
        Subject subject = inventoryItems.subjectWithoutObservations();
        inventoryService.populateObservations(subject, inventoryItems);
        avniSubjectRepository.create(subject);
    }

    public void processError(String inventoryUuid) throws Exception {
        HashMap<String, Object> inventoryItem = inventoryService.getImplementationInventory(inventoryUuid);
        if (inventoryItem == null) {
            logger.warn(String.format("Inventory Items has been deleted now: %s", inventoryUuid));
            updateErrorRecordAndSyncStatus(null, false, inventoryUuid);
            return;
        }
        process(inventoryItem);
    }

    @Override
    public void processDeletion(String deletedEntity) {
        processInventoryDeletion(deletedEntity);
    }

    @Override
    <T> void updateReadUptoDateTime(Map<String, Object> event) {
        IntegratingEntityStatus intEnt = integratingEntityStatusRepository.findByEntityType(entityType.name());
        String lastModifiedDate = (String) event.get("LastModifiedDate");
        if (StringUtils.hasText(lastModifiedDate)) {
            intEnt.setReadUptoDateTime(DateTimeUtil.convertToDate(lastModifiedDate));
            integratingEntityStatusRepository.save(intEnt);
        }
    }

    private void processInventoryDeletion(String deletedEntity) {
        try {
            logger.debug(String.format("Processing inventory deletion: externalId %s", deletedEntity));
            avniSubjectRepository.delete(deletedEntity);
        } catch (HttpClientErrorException.NotFound e) {
            logger.error(String.format("Failed to delete non-existent inventory: externalId %s", deletedEntity));
        } catch (Exception e) {
            logger.error(String.format("Failed to delete inventory: externalId %s", deletedEntity));
            throw e;
        }
    }
}
