package org.avni_integration_service.amrit.service;

import org.apache.log4j.Logger;
import org.avni_integration_service.amrit.config.AmritEntityType;
import org.avni_integration_service.amrit.config.AmritErrorType;
import org.avni_integration_service.amrit.config.AmritMappingDbConstants;
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
public class AvniAmritErrorService {
    private static final Logger logger = Logger.getLogger(AvniAmritErrorService.class);

    private final ErrorRecordRepository errorRecordRepository;
    private final IntegrationSystemRepository integrationSystemRepository;
    private final ErrorTypeRepository errorTypeRepository;

    @Autowired
    public AvniAmritErrorService(ErrorRecordRepository errorRecordRepository, IntegrationSystemRepository integrationSystemRepository, ErrorTypeRepository errorTypeRepository) {
        this.errorRecordRepository = errorRecordRepository;
        this.integrationSystemRepository = integrationSystemRepository;
        this.errorTypeRepository = errorTypeRepository;
    }

    public List<ErrorType> getUnprocessableErrorTypes() {
        return Arrays.asList( getErrorType(AmritErrorType.DummyErrorType));
    }

    private void saveAvniError(String uuid, AmritErrorType amritErrorType, AvniEntityType avniEntityType, String errorMsg) {
        ErrorType errorType = getErrorType(amritErrorType);
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

    private ErrorType getErrorType(AmritErrorType amritErrorType) {
        return errorTypeRepository.findByNameAndIntegrationSystem(amritErrorType.name(), integrationSystemRepository.findByName("Amrit"));
    }

    private ErrorRecord saveAmritError(String uuid, AmritErrorType amritErrorType, AmritEntityType AmritEntityType, String errorMsg) {
        ErrorRecord errorRecord = errorRecordRepository.findByIntegratingEntityTypeAndEntityId(AmritEntityType.name(), uuid);
        if (errorRecord != null && errorRecord.hasThisAsLastErrorType(getErrorType(amritErrorType))) {
            logger.info(String.format("Same error as the last processing for entity uuid %s, and type %s", uuid, AmritEntityType));
            if (!errorRecord.isProcessingDisabled()) {
                errorRecord.setProcessingDisabled(true);
                errorRecordRepository.save(errorRecord);
            }
        } else if (errorRecord != null && !errorRecord.hasThisAsLastErrorType(getErrorType(amritErrorType))) {
            logger.info(String.format("New error for entity uuid %s, and type %s", uuid, AmritEntityType));
            errorRecord.addErrorType(getErrorType(amritErrorType), errorMsg);
            errorRecordRepository.save(errorRecord);
        } else {
            errorRecord = new ErrorRecord();
            errorRecord.setIntegratingEntityType(AmritEntityType.name());
            errorRecord.setEntityId(uuid);
            errorRecord.addErrorType(getErrorType(amritErrorType), errorMsg);
            errorRecord.setProcessingDisabled(false);
            errorRecord.setIntegrationSystem(integrationSystemRepository.findByName(AmritMappingDbConstants.IntSystemName));
            errorRecordRepository.save(errorRecord);
        }
        return errorRecord;
    }


    public ErrorRecord errorOccurred(String entityUuid, AmritErrorType amritErrorType, AmritEntityType AmritEntityType, String errorMsg) {
        return saveAmritError(entityUuid, amritErrorType, AmritEntityType, errorMsg);
    }

    public void errorOccurred(String entityUuid, AmritErrorType amritErrorType, AvniEntityType avniEntityType, String errorMsg) {
        saveAvniError(entityUuid, amritErrorType, avniEntityType, errorMsg);
    }

    private void successfullyProcessedAmritEntity(AmritEntityType AmritEntityType, String uuid) {
        ErrorRecord errorRecord = errorRecordRepository.findByIntegratingEntityTypeAndEntityId(AmritEntityType.name(), uuid);
        if (errorRecord != null)
            errorRecordRepository.delete(errorRecord);
    }

    public void successfullyProcessed(String entityUUID, AmritEntityType AmritEntityType) {
        successfullyProcessedAmritEntity(AmritEntityType, entityUUID);
    }
}
