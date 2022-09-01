package org.avni_integration_service.bahmni.service;

import org.apache.log4j.Logger;
import org.avni_integration_service.avni.domain.Enrolment;
import org.avni_integration_service.avni.domain.GeneralEncounter;
import org.avni_integration_service.avni.domain.ProgramEncounter;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.bahmni.BahmniEntityType;
import org.avni_integration_service.bahmni.BahmniErrorType;
import org.avni_integration_service.bahmni.contract.OpenMRSFullEncounter;
import org.avni_integration_service.bahmni.contract.OpenMRSPatient;
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
public class AvniBahmniErrorService {
    private static final Logger logger = Logger.getLogger(AvniBahmniErrorService.class);

    private final ErrorRecordRepository errorRecordRepository;
    private final IntegrationSystemRepository integrationSystemRepository;
    private final ErrorTypeRepository errorTypeRepository;

    @Autowired
    public AvniBahmniErrorService(ErrorRecordRepository errorRecordRepository, IntegrationSystemRepository integrationSystemRepository, ErrorTypeRepository errorTypeRepository) {
        this.errorRecordRepository = errorRecordRepository;
        this.integrationSystemRepository = integrationSystemRepository;
        this.errorTypeRepository = errorTypeRepository;
    }

    public List<ErrorType> getUnprocessableErrorTypes() {
        return Arrays.asList(getErrorType(BahmniErrorType.NotACommunityMember), getErrorType(BahmniErrorType.EntityIsDeleted));
    }

    private void saveAvniError(String uuid, BahmniErrorType bahmniErrorType, AvniEntityType avniEntityType) {
        ErrorType errorType = getErrorType(bahmniErrorType);
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

    private ErrorType getErrorType(BahmniErrorType bahmniErrorType) {
        return errorTypeRepository.findByNameAndIntegrationSystem(bahmniErrorType.name(), integrationSystemRepository.findByName("bahmni"));
    }

    public boolean hasError(String entityId, BahmniEntityType bahmniEntityType) {
        return errorRecordRepository.findByIntegratingEntityTypeAndEntityId(bahmniEntityType.name(), entityId) != null;
    }

    public boolean hasError(String entityId, AvniEntityType avniEntityType, BahmniErrorType bahmniErrorType) {

        ErrorRecord errorRecord = errorRecordRepository.findByAvniEntityTypeAndEntityId(avniEntityType, entityId);
        return errorRecord != null && errorRecord.hasThisAsLastErrorType(getErrorType(bahmniErrorType));
    }

    public boolean hasAvniMultipleSubjectsError(String subjectId) {
        return hasError(subjectId, AvniEntityType.Subject, BahmniErrorType.MultipleSubjectsWithId);
    }

    private ErrorRecord saveBahmniError(String uuid, BahmniErrorType bahmniErrorType, BahmniEntityType bahmniEntityType) {
        ErrorRecord errorRecord = errorRecordRepository.findByIntegratingEntityTypeAndEntityId(bahmniEntityType.name(), uuid);
        if (errorRecord != null && errorRecord.hasThisAsLastErrorType(getErrorType(bahmniErrorType))) {
            logger.info(String.format("Same error as the last processing for entity uuid %s, and type %s", uuid, bahmniEntityType));
            if (!errorRecord.isProcessingDisabled()) {
                errorRecord.setProcessingDisabled(true);
                errorRecordRepository.save(errorRecord);
            }
        } else if (errorRecord != null && !errorRecord.hasThisAsLastErrorType(getErrorType(bahmniErrorType))) {
            logger.info(String.format("New error for entity uuid %s, and type %s", uuid, bahmniEntityType));
            errorRecord.addErrorType(getErrorType(bahmniErrorType));
            errorRecordRepository.save(errorRecord);
        } else {
            errorRecord = new ErrorRecord();
            errorRecord.setIntegratingEntityType(bahmniEntityType.name());
            errorRecord.setEntityId(uuid);
            errorRecord.addErrorType(getErrorType(bahmniErrorType));
            errorRecord.setProcessingDisabled(false);
            errorRecord.setIntegrationSystem(integrationSystemRepository.findByName("bahmni"));
            errorRecordRepository.save(errorRecord);
        }
        return errorRecord;
    }

    public void errorOccurred(Subject subject, BahmniErrorType bahmniErrorType) {
        saveAvniError(subject.getUuid(), bahmniErrorType, AvniEntityType.Subject);
    }

    public ErrorRecord errorOccurred(String entityUuid, BahmniErrorType bahmniErrorType, BahmniEntityType bahmniEntityType) {
        return saveBahmniError(entityUuid, bahmniErrorType, bahmniEntityType);
    }

    public void errorOccurred(String entityUuid, BahmniErrorType bahmniErrorType, AvniEntityType avniEntityType) {
        saveAvniError(entityUuid, bahmniErrorType, avniEntityType);
    }

    public void errorOccurred(Enrolment enrolment, BahmniErrorType bahmniErrorType) {
        saveAvniError(enrolment.getUuid(), bahmniErrorType, AvniEntityType.Enrolment);
    }

    public void errorOccurred(ProgramEncounter programEncounter, BahmniErrorType bahmniErrorType) {
        saveAvniError(programEncounter.getUuid(), bahmniErrorType, AvniEntityType.ProgramEncounter);
    }

    public void errorOccurred(GeneralEncounter generalEncounter, BahmniErrorType bahmniErrorType) {
        saveAvniError(generalEncounter.getUuid(), bahmniErrorType, AvniEntityType.GeneralEncounter);
    }

    public void errorOccurred(OpenMRSPatient patient, BahmniErrorType bahmniErrorType) {
        saveBahmniError(patient.getUuid(), bahmniErrorType, BahmniEntityType.Patient);
    }

    public ErrorRecord errorOccurred(OpenMRSFullEncounter openMRSFullEncounter, BahmniErrorType bahmniErrorType) {
        return saveBahmniError(openMRSFullEncounter.getUuid(), bahmniErrorType, BahmniEntityType.Encounter);
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
