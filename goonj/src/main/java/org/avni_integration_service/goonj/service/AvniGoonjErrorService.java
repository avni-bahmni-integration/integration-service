package org.avni_integration_service.goonj.service;

import org.apache.log4j.Logger;
import org.avni_integration_service.avni.domain.Enrolment;
import org.avni_integration_service.avni.domain.GeneralEncounter;
import org.avni_integration_service.avni.domain.ProgramEncounter;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.goonj.GoonjEntityType;
import org.avni_integration_service.goonj.GoonjErrorType;
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

    @Autowired
    public AvniGoonjErrorService(ErrorRecordRepository errorRecordRepository, IntegrationSystemRepository integrationSystemRepository, ErrorTypeRepository errorTypeRepository) {
        this.errorRecordRepository = errorRecordRepository;
        this.integrationSystemRepository = integrationSystemRepository;
        this.errorTypeRepository = errorTypeRepository;
    }

    public List<ErrorType> getUnprocessableErrorTypes() {
        return Arrays.asList( getErrorType(GoonjErrorType.EntityIsDeleted));
    }

    private void saveAvniError(String uuid, GoonjErrorType goonjErrorType, AvniEntityType avniEntityType) {
        ErrorType errorType = getErrorType(goonjErrorType);
        ErrorRecord errorRecord = errorRecordRepository.findByAvniEntityTypeAndEntityId(avniEntityType, uuid);
        if (errorRecord != null && errorRecord.hasThisAsLastErrorType(errorType)) {
            logger.info(String.format("Same error as the last processing for entity uuid %s, and type %s", uuid, avniEntityType));
            if (!errorRecord.isProcessingDisabled()) {
                errorRecord.setProcessingDisabled(true);
                errorRecordRepository.save(errorRecord);
            }
        } else if (errorRecord != null && !errorRecord.hasThisAsLastErrorType(errorType)) {
            errorRecord.addErrorType(errorType);
            errorRecordRepository.save(errorRecord);
        } else {
            errorRecord = new ErrorRecord();
            errorRecord.setAvniEntityType(avniEntityType);
            errorRecord.setEntityId(uuid);
            errorRecord.addErrorType(errorType);
            errorRecord.setProcessingDisabled(false);
            errorRecordRepository.save(errorRecord);
        }
    }

    private ErrorType getErrorType(GoonjErrorType goonjErrorType) {
        return errorTypeRepository.findByNameAndIntegrationSystem(goonjErrorType.name(), integrationSystemRepository.findByName("Goonj"));
    }

    public boolean hasError(String entityId, GoonjEntityType goonjEntityType) {
        return errorRecordRepository.findByIntegratingEntityTypeAndEntityId(goonjEntityType.name(), entityId) != null;
    }

    public boolean hasError(String entityId, AvniEntityType avniEntityType, GoonjErrorType goonjErrorType) {

        ErrorRecord errorRecord = errorRecordRepository.findByAvniEntityTypeAndEntityId(avniEntityType, entityId);
        return errorRecord != null && errorRecord.hasThisAsLastErrorType(getErrorType(goonjErrorType));
    }

    public boolean hasAvniMultipleSubjectsError(String subjectId) {
        return hasError(subjectId, AvniEntityType.Subject, GoonjErrorType.MultipleSubjectsWithId);
    }

    private ErrorRecord saveGoonjError(String uuid, GoonjErrorType goonjErrorType, GoonjEntityType goonjEntityType) {
        ErrorRecord errorRecord = errorRecordRepository.findByIntegratingEntityTypeAndEntityId(goonjEntityType.name(), uuid);
        if (errorRecord != null && errorRecord.hasThisAsLastErrorType(getErrorType(goonjErrorType))) {
            logger.info(String.format("Same error as the last processing for entity uuid %s, and type %s", uuid, goonjEntityType));
            if (!errorRecord.isProcessingDisabled()) {
                errorRecord.setProcessingDisabled(true);
                errorRecordRepository.save(errorRecord);
            }
        } else if (errorRecord != null && !errorRecord.hasThisAsLastErrorType(getErrorType(goonjErrorType))) {
            logger.info(String.format("New error for entity uuid %s, and type %s", uuid, goonjEntityType));
            errorRecord.addErrorType(getErrorType(goonjErrorType));
            errorRecordRepository.save(errorRecord);
        } else {
            errorRecord = new ErrorRecord();
            errorRecord.setIntegratingEntityType(goonjEntityType.name());
            errorRecord.setEntityId(uuid);
            errorRecord.addErrorType(getErrorType(goonjErrorType));
            errorRecord.setProcessingDisabled(false);
            errorRecord.setIntegrationSystem(integrationSystemRepository.findByName("Goonj"));
            errorRecordRepository.save(errorRecord);
        }
        return errorRecord;
    }

    public void errorOccurred(Subject subject, GoonjErrorType goonjErrorType) {
        saveAvniError(subject.getUuid(), goonjErrorType, AvniEntityType.Subject);
    }

    public ErrorRecord errorOccurred(String entityUuid, GoonjErrorType goonjErrorType, GoonjEntityType goonjEntityType) {
        return saveGoonjError(entityUuid, goonjErrorType, goonjEntityType);
    }

    public void errorOccurred(String entityUuid, GoonjErrorType goonjErrorType, AvniEntityType avniEntityType) {
        saveAvniError(entityUuid, goonjErrorType, avniEntityType);
    }

    public void errorOccurred(Enrolment enrolment, GoonjErrorType goonjErrorType) {
        saveAvniError(enrolment.getUuid(), goonjErrorType, AvniEntityType.Enrolment);
    }

    public void errorOccurred(ProgramEncounter programEncounter, GoonjErrorType goonjErrorType) {
        saveAvniError(programEncounter.getUuid(), goonjErrorType, AvniEntityType.ProgramEncounter);
    }

    public void errorOccurred(GeneralEncounter generalEncounter, GoonjErrorType goonjErrorType) {
        saveAvniError(generalEncounter.getUuid(), goonjErrorType, AvniEntityType.GeneralEncounter);
    }

    private void successfullyProcessedAvniEntity(AvniEntityType avniEntityType, String uuid) {
        ErrorRecord errorRecord = errorRecordRepository.findByAvniEntityTypeAndEntityId(avniEntityType, uuid);
        if (errorRecord != null)
            errorRecordRepository.delete(errorRecord);
    }

    private void successfullyProcessedGoonjEntity(GoonjEntityType goonjEntityType, String uuid) {
        ErrorRecord errorRecord = errorRecordRepository.findByIntegratingEntityTypeAndEntityId(goonjEntityType.name(), uuid);
        if (errorRecord != null)
            errorRecordRepository.delete(errorRecord);
    }

    public void successfullyProcessed(Subject subject) {
        successfullyProcessedAvniEntity(AvniEntityType.Subject, subject.getUuid());
    }

    public void successfullyProcessed(Enrolment enrolment) {
        successfullyProcessedAvniEntity(AvniEntityType.Enrolment, enrolment.getUuid());
    }

    public void successfullyProcessed(ProgramEncounter programEncounter) {
        successfullyProcessedAvniEntity(AvniEntityType.ProgramEncounter, programEncounter.getUuid());
    }

    public void successfullyProcessed(GeneralEncounter generalEncounter) {
        successfullyProcessedAvniEntity(AvniEntityType.GeneralEncounter, generalEncounter.getUuid());
    }

    public void successfullyProcessed(String entityUUID, GoonjEntityType goonjEntityType) {
        successfullyProcessedGoonjEntity(goonjEntityType, entityUUID);
    }
}
