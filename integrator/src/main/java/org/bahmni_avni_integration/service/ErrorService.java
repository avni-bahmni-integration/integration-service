package org.bahmni_avni_integration.service;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.contract.avni.Enrolment;
import org.bahmni_avni_integration.contract.avni.ProgramEncounter;
import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSFullEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSPatient;
import org.bahmni_avni_integration.integration_data.BahmniEntityType;
import org.bahmni_avni_integration.integration_data.domain.AvniEntityType;
import org.bahmni_avni_integration.integration_data.domain.ErrorRecord;
import org.bahmni_avni_integration.integration_data.domain.ErrorType;
import org.bahmni_avni_integration.integration_data.repository.ErrorRecordRepository;
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
            return;
        }

        errorRecord = new ErrorRecord();
        errorRecord.setAvniEntityType(avniEntityType);
        errorRecord.setEntityId(uuid);
        errorRecord.addErrorType(errorType);
        errorRecord.setProcessingDisabled(false);
        errorRecordRepository.save(errorRecord);
    }

    public boolean hasError(String entityId, BahmniEntityType bahmniEntityType) {
        return errorRecordRepository.findByBahmniEntityTypeAndEntityId(bahmniEntityType, entityId) != null;
    }

    public boolean hasError(String entityId, AvniEntityType avniEntityType, ErrorType errorType) {
        ErrorRecord errorRecord = errorRecordRepository.findByAvniEntityTypeAndEntityId(avniEntityType, entityId);
        return errorRecord != null && errorRecord.hasThisAsLastErrorType(errorType);
    }

    public boolean hasAvniMultipleSubjectsError(String subjectId) {
        return hasError(subjectId, AvniEntityType.Subject, ErrorType.MultipleSubjectsWithId);
    }

    private ErrorRecord saveBahmniError(String uuid, ErrorType errorType, BahmniEntityType bahmniEntityType) {
        ErrorRecord errorRecord = errorRecordRepository.findByBahmniEntityTypeAndEntityId(bahmniEntityType, uuid);
        if (errorRecord != null && errorRecord.hasThisAsLastErrorType(errorType)) {
            logger.info(String.format("Same error as the last processing for entity uuid %s, and type %s", uuid, bahmniEntityType));
            return errorRecord;
        }

        errorRecord = new ErrorRecord();
        errorRecord.setBahmniEntityType(bahmniEntityType);
        errorRecord.setEntityId(uuid);
        errorRecord.addErrorType(errorType);
        errorRecord.setProcessingDisabled(false);
        return errorRecordRepository.save(errorRecord);
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
        ErrorRecord errorRecord = errorRecordRepository.findByBahmniEntityTypeAndEntityId(bahmniEntityType, uuid);
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

    public void successfullyProcessed(OpenMRSPatient openMRSPatient) {
        successfullyProcessedBahmniEntity(BahmniEntityType.Patient, openMRSPatient.getUuid());
    }

    public void successfullyProcessed(OpenMRSFullEncounter openMRSFullEncounter) {
        successfullyProcessedBahmniEntity(BahmniEntityType.Encounter, openMRSFullEncounter.getUuid());
    }
}
