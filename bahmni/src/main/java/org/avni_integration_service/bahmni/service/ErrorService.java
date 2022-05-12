package org.avni_integration_service.bahmni.service;

import org.apache.log4j.Logger;
import org.avni_integration_service.bahmni.BahmniEntityType;
import org.avni_integration_service.contract.avni.Enrolment;
import org.avni_integration_service.contract.avni.GeneralEncounter;
import org.avni_integration_service.contract.avni.ProgramEncounter;
import org.avni_integration_service.contract.avni.Subject;
import org.avni_integration_service.bahmni.contract.OpenMRSFullEncounter;
import org.avni_integration_service.bahmni.contract.OpenMRSPatient;
import org.avni_integration_service.integration_data.domain.AvniEntityType;
import org.avni_integration_service.integration_data.domain.ErrorRecord;
import org.avni_integration_service.integration_data.domain.ErrorType;
import org.avni_integration_service.integration_data.repository.ErrorRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ErrorService {
    private static final Logger logger = Logger.getLogger(ErrorService.class);

    @Autowired
    private ErrorRecordRepository errorRecordRepository;

    private void saveAvniError(String uuid, ErrorType errorType, AvniEntityType avniEntityType) {
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

    public boolean hasError(String entityId, BahmniEntityType bahmniEntityType) {
        return errorRecordRepository.findByIntegratingEntityTypeAndEntityId(bahmniEntityType.name(), entityId) != null;
    }

    public boolean hasError(String entityId, AvniEntityType avniEntityType, ErrorType errorType) {
        ErrorRecord errorRecord = errorRecordRepository.findByAvniEntityTypeAndEntityId(avniEntityType, entityId);
        return errorRecord != null && errorRecord.hasThisAsLastErrorType(errorType);
    }

    public boolean hasAvniMultipleSubjectsError(String subjectId) {
        return hasError(subjectId, AvniEntityType.Subject, ErrorType.MultipleSubjectsWithId);
    }

    private ErrorRecord saveBahmniError(String uuid, ErrorType errorType, BahmniEntityType bahmniEntityType) {
        ErrorRecord errorRecord = errorRecordRepository.findByIntegratingEntityTypeAndEntityId(bahmniEntityType.name(), uuid);
        if (errorRecord != null && errorRecord.hasThisAsLastErrorType(errorType)) {
            logger.info(String.format("Same error as the last processing for entity uuid %s, and type %s", uuid, bahmniEntityType));
            if (!errorRecord.isProcessingDisabled()) {
                errorRecord.setProcessingDisabled(true);
                errorRecordRepository.save(errorRecord);
            }
        } else if (errorRecord != null && !errorRecord.hasThisAsLastErrorType(errorType)) {
            logger.info(String.format("New error for entity uuid %s, and type %s", uuid, bahmniEntityType));
            errorRecord.addErrorType(errorType);
            errorRecordRepository.save(errorRecord);
        } else {
            errorRecord = new ErrorRecord();
            errorRecord.setIntegratingEntityType(bahmniEntityType.name());
            errorRecord.setEntityId(uuid);
            errorRecord.addErrorType(errorType);
            errorRecord.setProcessingDisabled(false);
            errorRecordRepository.save(errorRecord);
        }
        return errorRecord;
    }

    public void errorOccurred(Subject subject, ErrorType errorType) {
        saveAvniError(subject.getUuid(), errorType, AvniEntityType.Subject);
    }

    public ErrorRecord errorOccurred(String entityUuid, ErrorType errorType, BahmniEntityType bahmniEntityType) {
        return saveBahmniError(entityUuid, errorType, bahmniEntityType);
    }

    public void errorOccurred(String entityUuid, ErrorType errorType, AvniEntityType avniEntityType) {
        saveAvniError(entityUuid, errorType, avniEntityType);
    }

    public void errorOccurred(Enrolment enrolment, ErrorType errorType) {
        saveAvniError(enrolment.getUuid(), errorType, AvniEntityType.Enrolment);
    }

    public void errorOccurred(ProgramEncounter programEncounter, ErrorType errorType) {
        saveAvniError(programEncounter.getUuid(), errorType, AvniEntityType.ProgramEncounter);
    }

    public void errorOccurred(GeneralEncounter generalEncounter, ErrorType errorType) {
        saveAvniError(generalEncounter.getUuid(), errorType, AvniEntityType.GeneralEncounter);
    }

    public void errorOccurred(OpenMRSPatient patient, ErrorType errorType) {
        saveBahmniError(patient.getUuid(), errorType, BahmniEntityType.Patient);
    }

    public ErrorRecord errorOccurred(OpenMRSFullEncounter openMRSFullEncounter, ErrorType errorType) {
        return saveBahmniError(openMRSFullEncounter.getUuid(), errorType, BahmniEntityType.Encounter);
    }

    private void successfullyProcessedAvniEntity(AvniEntityType avniEntityType, String uuid) {
        ErrorRecord errorRecord = errorRecordRepository.findByAvniEntityTypeAndEntityId(avniEntityType, uuid);
        if (errorRecord != null)
            errorRecordRepository.delete(errorRecord);
    }

    private void successfullyProcessedBahmniEntity(BahmniEntityType bahmniEntityType, String uuid) {
        ErrorRecord errorRecord = errorRecordRepository.findByIntegratingEntityTypeAndEntityId(bahmniEntityType.name(), uuid);
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

    public void successfullyProcessed(OpenMRSPatient openMRSPatient) {
        successfullyProcessedBahmniEntity(BahmniEntityType.Patient, openMRSPatient.getUuid());
    }

    public void successfullyProcessed(OpenMRSFullEncounter openMRSFullEncounter) {
        successfullyProcessedBahmniEntity(BahmniEntityType.Encounter, openMRSFullEncounter.getUuid());
    }
}