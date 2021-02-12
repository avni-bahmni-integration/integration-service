package org.bahmni_avni_integration.service;

import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.domain.AvniEntityType;
import org.bahmni_avni_integration.domain.ErrorRecord;
import org.bahmni_avni_integration.domain.ErrorType;
import org.bahmni_avni_integration.repository.ErrorRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ErrorService {
    @Autowired
    private ErrorRecordRepository errorRecordRepository;

    public void errorOccurred(Subject subject, ErrorType errorType) {
        ErrorRecord errorRecord = errorRecordRepository.findByAvniEntityTypeAndSubjectPatientExternalIdAndErrorType(AvniEntityType.Subject, subject.getUuid(), errorType);
        if (errorRecord != null) return;

        errorRecord = new ErrorRecord();
        errorRecord.setAvniEntityType(AvniEntityType.Subject);
        errorRecord.setSubjectPatientExternalId(subject.getUuid());
        errorRecord.setErrorType(errorType);
        errorRecordRepository.save(errorRecord);
    }

    public void successfullyProcessed(Subject subject) {
        List<ErrorRecord> errorRecords = errorRecordRepository.findAllByAvniEntityTypeAndSubjectPatientExternalId(AvniEntityType.Subject, subject.getUuid());
        errorRecordRepository.deleteAll(errorRecords);
    }
}