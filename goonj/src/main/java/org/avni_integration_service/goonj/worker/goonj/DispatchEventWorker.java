package org.avni_integration_service.goonj.worker.goonj;

import org.apache.log4j.Logger;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.avni.repository.AvniSubjectRepository;
import org.avni_integration_service.avni.worker.ErrorRecordWorker;
import org.avni_integration_service.goonj.GoonjEntityType;
import org.avni_integration_service.goonj.GoonjErrorType;
import org.avni_integration_service.goonj.config.GoonjContextProvider;
import org.avni_integration_service.goonj.domain.Dispatch;
import org.avni_integration_service.goonj.dto.DeletedDispatchStatusLineItem;
import org.avni_integration_service.goonj.service.AvniGoonjErrorService;
import org.avni_integration_service.goonj.service.DispatchService;
import org.avni_integration_service.integration_data.domain.IntegrationSystem;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.avni_integration_service.integration_data.service.error.ErrorClassifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DispatchEventWorker extends GoonjEventWorker implements ErrorRecordWorker {
    private static final Logger logger = Logger.getLogger(DispatchEventWorker.class);
    private final DispatchService dispatchService;
    private final AvniSubjectRepository avniSubjectRepository;

    @Autowired
    public DispatchEventWorker(DispatchService dispatchService, AvniGoonjErrorService avniGoonjErrorService,
                               AvniSubjectRepository avniSubjectRepository, IntegratingEntityStatusRepository integratingEntityStatusRepository,
                               ErrorClassifier errorClassifier, GoonjContextProvider goonjContextProvider) {
        super(avniGoonjErrorService, integratingEntityStatusRepository, GoonjEntityType.Dispatch, errorClassifier, goonjContextProvider);
        this.dispatchService = dispatchService;
        this.avniSubjectRepository = avniSubjectRepository;
    }

    public void process(Map<String, Object> event) throws Exception {
        try {
            processDispatch(event);
            updateErrorRecordAndSyncStatus(event, true, (String) event.get("DispatchStatusId"));
        } catch (Exception exception) {
            handleError(event, exception, "DispatchStatusId", GoonjErrorType.DispatchAttributesMismatch);
        }
    }

    private void processDispatch(Map<String, Object> dispatchResponse) {
        logger.debug(String.format("Processing dispatch: name %s || uuid %s", dispatchResponse.get("DispatchStatusName"), dispatchResponse.get("DispatchStatusId")));
        Dispatch dispatch = Dispatch.from(dispatchResponse);
        Subject subject = dispatch.subjectWithoutObservations();
        dispatchService.populateObservations(subject, dispatch);
        avniSubjectRepository.create(subject);
    }

    public void processError(String dispatchUuid) throws Exception {
        HashMap<String, Object> dispatch = dispatchService.getDispatch(dispatchUuid);
        if (dispatch == null) {
            logger.warn(String.format("Dispatch has been deleted now: %s", dispatchUuid));
            updateErrorRecordAndSyncStatus(null, false, dispatchUuid);
            return;
        }
        process(dispatch);
    }

    @Override
    public void processDeletion(String deletedEntity) {
        processDispatchDeletion(deletedEntity);
    }

    public void processDispatchLineItemDeletion(DeletedDispatchStatusLineItem deletedEntity) {
        try {
            logger.debug(String.format("Processing dispatch line items deletion: externalId %s", deletedEntity.getDispatchStatusLineItemId()));
            processDispatchStatusLineItemDeletion(deletedEntity);
            updateErrorRecordAndSyncStatus(null, false, deletedEntity.getDispatchStatusLineItemId());
        } catch (Exception e) {
            logger.error(String.format("Failed to delete dispatch line items: externalId %s", deletedEntity.getDispatchStatusLineItemId()));
            createOrUpdateErrorRecordAndSyncStatus(null, false, deletedEntity.getDispatchStatusLineItemId(),
                    GoonjErrorType.DispatchLineItemsDeletionFailure, e.getLocalizedMessage());
        }
    }

    private void processDispatchStatusLineItemDeletion(DeletedDispatchStatusLineItem deletedEntity) {
        if(deletedEntity.getDispatchStatusId() == null || deletedEntity.getDispatchStatusLineItemId() == null) {
            return;
        }
        Subject dispatchStatus = avniSubjectRepository.getSubject((String) deletedEntity.getDispatchStatusId());
        List<HashMap<String, Object>> materialsDispatched = (List<HashMap<String, Object>>) dispatchStatus
                .getObservation("Materials Dispatched");
        if(materialsDispatched != null && materialsDispatched.size() > 0) {
            materialsDispatched.removeIf(md -> md.get("Dispatch Line Item Id").equals(deletedEntity.getDispatchStatusLineItemId()));
        }
        avniSubjectRepository.update(dispatchStatus);
    }

    private void processDispatchDeletion(String deletedEntity) {
        try {
            logger.debug(String.format("Processing dispatch deletion: externalId %s", deletedEntity));
            avniSubjectRepository.delete(deletedEntity);
        } catch (HttpClientErrorException.NotFound e) {
            logger.error(String.format("Failed to delete non-existent dispatch: externalId %s", deletedEntity));
        } catch (Exception e) {
            logger.error(String.format("Failed to delete dispatch: externalId %s", deletedEntity));
            throw e;
        }
    }
}
