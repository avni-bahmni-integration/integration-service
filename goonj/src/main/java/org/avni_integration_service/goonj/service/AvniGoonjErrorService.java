package org.avni_integration_service.goonj.service;

import org.apache.log4j.Logger;
import org.avni_integration_service.goonj.GoonjEntityType;
import org.avni_integration_service.goonj.GoonjErrorType;
import org.avni_integration_service.goonj.config.GoonjContextProvider;
import org.avni_integration_service.integration_data.domain.AvniEntityType;
import org.avni_integration_service.integration_data.domain.error.ErrorRecord;
import org.avni_integration_service.integration_data.domain.error.ErrorType;
import org.avni_integration_service.integration_data.repository.ErrorRecordRepository;
import org.avni_integration_service.integration_data.repository.ErrorTypeRepository;
import org.avni_integration_service.integration_data.repository.IntegrationSystemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class AvniGoonjErrorService {
    private static final Logger logger = Logger.getLogger(AvniGoonjErrorService.class);

    private final ErrorRecordRepository errorRecordRepository;
    private final IntegrationSystemRepository integrationSystemRepository;
    private final ErrorTypeRepository errorTypeRepository;
    private final GoonjContextProvider goonjContextProvider;

    @Autowired
    public AvniGoonjErrorService(ErrorRecordRepository errorRecordRepository, IntegrationSystemRepository integrationSystemRepository, ErrorTypeRepository errorTypeRepository, GoonjContextProvider goonjContextProvider) {
        this.errorRecordRepository = errorRecordRepository;
        this.integrationSystemRepository = integrationSystemRepository;
        this.errorTypeRepository = errorTypeRepository;
        this.goonjContextProvider = goonjContextProvider;
    }

    public List<ErrorType> getUnprocessableErrorTypes() {
        return Arrays.asList( getErrorType(GoonjErrorType.EntityIsDeleted));
    }

    private void saveAvniError(String uuid, GoonjErrorType goonjErrorType, AvniEntityType avniEntityType, String errorMsg) {
        ErrorType errorType = getErrorType(goonjErrorType);
        ErrorRecord errorRecord = errorRecordRepository.findByAvniEntityTypeAndEntityId(avniEntityType, uuid);
        if (errorRecord != null && errorRecord.hasThisAsLastErrorType(errorType)) {
            logger.info(String.format("Same error as the last processing for entity uuid %s, and type %s", uuid, avniEntityType));
            if (!errorRecord.isProcessingDisabled()) {
                errorRecord.setProcessingDisabled(true);
                errorRecordRepository.save(errorRecord);
            }
        } else if (errorRecord != null && !errorRecord.hasThisAsLastErrorType(errorType)) {
            errorRecord.addErrorType(errorType, errorMsg);
            errorRecordRepository.save(errorRecord);
        } else {
            errorRecord = new ErrorRecord();
            errorRecord.setAvniEntityType(avniEntityType);
            errorRecord.setEntityId(uuid);
            errorRecord.addErrorType(errorType, errorMsg);
            errorRecord.setProcessingDisabled(false);
            errorRecordRepository.save(errorRecord);
        }
    }

    private ErrorType getErrorType(GoonjErrorType goonjErrorType) {
        return errorTypeRepository.findByNameAndIntegrationSystem(goonjErrorType.name(), goonjContextProvider.get().getIntegrationSystem());
    }

    private ErrorRecord saveGoonjError(String uuid, GoonjErrorType goonjErrorType, GoonjEntityType goonjEntityType, String errorMsg) {
        ErrorRecord errorRecord = errorRecordRepository.findByIntegratingEntityTypeAndEntityId(goonjEntityType.name(), uuid);
        if (errorRecord != null && errorRecord.hasThisAsLastErrorType(getErrorType(goonjErrorType))) {
            logger.info(String.format("Same error as the last processing for entity uuid %s, and type %s", uuid, goonjEntityType));
            if (!errorRecord.isProcessingDisabled()) {
                errorRecord.setProcessingDisabled(true);
                errorRecordRepository.save(errorRecord);
            }
        } else if (errorRecord != null && !errorRecord.hasThisAsLastErrorType(getErrorType(goonjErrorType))) {
            logger.info(String.format("New error for entity uuid %s, and type %s", uuid, goonjEntityType));
            errorRecord.addErrorType(getErrorType(goonjErrorType), errorMsg);
            errorRecordRepository.save(errorRecord);
        } else {
            errorRecord = new ErrorRecord();
            errorRecord.setIntegratingEntityType(goonjEntityType.name());
            errorRecord.setEntityId(uuid);
            errorRecord.addErrorType(getErrorType(goonjErrorType), errorMsg);
            errorRecord.setProcessingDisabled(false);
            errorRecord.setIntegrationSystem(integrationSystemRepository.findEntity(goonjContextProvider.get().getIntegrationSystem().getId()));
            errorRecordRepository.save(errorRecord);
        }
        return errorRecord;
    }


    public ErrorRecord errorOccurred(String entityUuid, GoonjErrorType goonjErrorType, GoonjEntityType goonjEntityType, String errorMsg) {
        return saveGoonjError(entityUuid, goonjErrorType, goonjEntityType, errorMsg);
    }

    public void errorOccurred(String entityUuid, GoonjErrorType goonjErrorType, AvniEntityType avniEntityType, String errorMsg) {
        saveAvniError(entityUuid, goonjErrorType, avniEntityType, errorMsg);
    }

    private void successfullyProcessedGoonjEntity(GoonjEntityType goonjEntityType, String uuid) {
        ErrorRecord errorRecord = errorRecordRepository.findByIntegratingEntityTypeAndEntityId(goonjEntityType.name(), uuid);
        if (errorRecord != null)
            errorRecordRepository.delete(errorRecord);
    }

    public void successfullyProcessed(String entityUUID, GoonjEntityType goonjEntityType) {
        successfullyProcessedGoonjEntity(goonjEntityType, entityUUID);
    }
}
