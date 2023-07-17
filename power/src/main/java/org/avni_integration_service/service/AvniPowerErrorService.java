package org.avni_integration_service.service;

import org.apache.log4j.Logger;
import org.avni_integration_service.config.PowerEntityType;
import org.avni_integration_service.config.PowerErrorType;
import org.avni_integration_service.integration_data.domain.IntegrationSystem;
import org.avni_integration_service.integration_data.domain.error.ErrorRecord;
import org.avni_integration_service.integration_data.domain.error.ErrorType;
import org.avni_integration_service.integration_data.repository.ErrorRecordRepository;
import org.avni_integration_service.integration_data.repository.ErrorTypeRepository;
import org.avni_integration_service.integration_data.repository.IntegrationSystemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class AvniPowerErrorService {
    private static final Logger logger = Logger.getLogger(AvniPowerErrorService.class);

    private final ErrorRecordRepository errorRecordRepository;
    private final IntegrationSystemRepository integrationSystemRepository;
    private final ErrorTypeRepository errorTypeRepository;

    @Autowired
    public AvniPowerErrorService(ErrorRecordRepository errorRecordRepository, IntegrationSystemRepository integrationSystemRepository, ErrorTypeRepository errorTypeRepository) {
        this.errorRecordRepository = errorRecordRepository;
        this.integrationSystemRepository = integrationSystemRepository;
        this.errorTypeRepository = errorTypeRepository;
    }

    private ErrorType getErrorType(PowerErrorType powerErrorType) {
        return errorTypeRepository.findByNameAndIntegrationSystem(powerErrorType.name(), integrationSystemRepository.findBySystemType(IntegrationSystem.IntegrationSystemType.power));
    }

    public List<ErrorType> getUnprocessableErrorTypes() {
        return Collections.singletonList(getErrorType(PowerErrorType.CallSidDeleted));
    }

    private ErrorRecord saveExotelError(String uuid, PowerErrorType powerErrorType, PowerEntityType powerEntityType) {
        ErrorRecord errorRecord = errorRecordRepository.findByIntegratingEntityTypeAndEntityId(powerEntityType.name(), uuid);
        if (errorRecord != null && errorRecord.hasThisAsLastErrorType(getErrorType(powerErrorType))) {
            logger.info(String.format("Same error as the last processing for entity uuid %s, and type %s", uuid, powerEntityType));
            if (!errorRecord.isProcessingDisabled()) {
                errorRecord.setProcessingDisabled(true);
                errorRecordRepository.save(errorRecord);
            }
        } else if (errorRecord != null && !errorRecord.hasThisAsLastErrorType(getErrorType(powerErrorType))) {
            logger.info(String.format("New error for entity uuid %s, and type %s", uuid, powerEntityType));
            errorRecord.addErrorType(getErrorType(powerErrorType));
            errorRecordRepository.save(errorRecord);
        } else {
            errorRecord = new ErrorRecord();
            errorRecord.setIntegratingEntityType(powerEntityType.name());
            errorRecord.setEntityId(uuid);
            errorRecord.addErrorType(getErrorType(powerErrorType));
            errorRecord.setProcessingDisabled(false);
            errorRecord.setIntegrationSystem(integrationSystemRepository.findBySystemType(IntegrationSystem.IntegrationSystemType.power));
            errorRecordRepository.save(errorRecord);
        }
        return errorRecord;
    }

    public ErrorRecord errorOccurred(String entityUuid, PowerErrorType powerErrorType, PowerEntityType powerEntityType) {
        return saveExotelError(entityUuid, powerErrorType, powerEntityType);
    }

    private void successfullyProcessedPowerEntity(PowerEntityType powerEntityType, String uuid) {
        ErrorRecord errorRecord = errorRecordRepository.findByIntegratingEntityTypeAndEntityId(powerEntityType.name(), uuid);
        if (errorRecord != null)
            errorRecordRepository.delete(errorRecord);
    }

    public void successfullyProcessed(String entityUUID, PowerEntityType powerEntityType) {
        successfullyProcessedPowerEntity(powerEntityType, entityUUID);
    }
}
