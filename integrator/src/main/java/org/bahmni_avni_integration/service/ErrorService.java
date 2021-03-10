package org.bahmni_avni_integration.service;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.integration_data.BahmniEntityType;
import org.bahmni_avni_integration.contract.avni.Enrolment;
import org.bahmni_avni_integration.contract.avni.GeneralEncounter;
import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSPatient;
import org.bahmni_avni_integration.integration_data.internal.SubjectToPatientMetaData;
import org.bahmni_avni_integration.integration_data.domain.AvniEntityType;
import org.bahmni_avni_integration.integration_data.domain.ErrorRecord;
import org.bahmni_avni_integration.integration_data.domain.ErrorType;
import org.bahmni_avni_integration.integration_data.repository.ErrorRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ErrorService {
    private static Logger logger = Logger.getLogger(ErrorService.class);

    @Autowired
    private ErrorRecordRepository errorRecordRepository;

    public void errorOccurred(Subject subject, ErrorType errorType, SubjectToPatientMetaData metaData) {
        ErrorRecord errorRecord = errorRecordRepository.findByAvniEntityTypeAndSubjectPatientExternalIdAndErrorType(AvniEntityType.Subject, subject.getUuid(), errorType);
        if (errorRecord != null) return;

        String id = subject.getId(metaData);
        logger.warn(String.format("Patient subject id %s not found", id));

        errorRecord = new ErrorRecord();
        errorRecord.setAvniEntityType(AvniEntityType.Subject);
        errorRecord.setSubjectPatientExternalId(subject.getUuid());
        errorRecord.setSubjectPatientId(id);
        errorRecord.setErrorType(errorType);
        errorRecordRepository.save(errorRecord);
    }

    public void errorOccurred(OpenMRSPatient patient, ErrorType errorType) {
        ErrorRecord errorRecord = errorRecordRepository.findByBahmniEntityTypeAndSubjectPatientExternalIdAndErrorType(BahmniEntityType.Patient, patient.getUuid(), errorType);
        if (errorRecord != null) return;

        errorRecord = new ErrorRecord();
        errorRecord.setBahmniEntityType(BahmniEntityType.Patient);
        errorRecord.setSubjectPatientExternalId(patient.getUuid());
        errorRecord.setSubjectPatientId(patient.getPatientId());
        errorRecord.setErrorType(errorType);
        errorRecordRepository.save(errorRecord);
    }

    public void errorOccurred(GeneralEncounter existingEncounter, ErrorType errorType) {
        ErrorRecord errorRecord = errorRecordRepository.findByBahmniEntityTypeAndEncounterExternalIdAndErrorType(BahmniEntityType.Encounter, existingEncounter.getUuid(), errorType);
        if (errorRecord != null) return;

        errorRecord = new ErrorRecord();
        errorRecord.setBahmniEntityType(BahmniEntityType.Encounter);
        errorRecord.setSubjectPatientExternalId(existingEncounter.getSubjectExternalId());
        errorRecord.setEncounterExternalId(existingEncounter.getUuid());
        errorRecord.setErrorType(errorType);
        errorRecordRepository.save(errorRecord);
    }

    public void successfullyProcessed(Subject subject) {
        List<ErrorRecord> errorRecords = errorRecordRepository.findAllByAvniEntityTypeAndSubjectPatientExternalId(AvniEntityType.Subject, subject.getUuid());
        errorRecordRepository.deleteAll(errorRecords);
    }

    public void successfullyProcessed(Enrolment enrolment) {
        List<ErrorRecord> errorRecords = errorRecordRepository.findAllByAvniEntityTypeAndEnrolmentExternalId(AvniEntityType.Enrolment, enrolment.getUuid());
        errorRecordRepository.deleteAll(errorRecords);
    }
}