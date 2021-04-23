package org.bahmni_avni_integration.worker;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.integration_data.BahmniEntityType;
import org.bahmni_avni_integration.integration_data.domain.AvniEntityType;
import org.bahmni_avni_integration.integration_data.domain.ErrorRecord;
import org.bahmni_avni_integration.integration_data.domain.ErrorType;
import org.bahmni_avni_integration.integration_data.repository.ErrorRecordRepository;
import org.bahmni_avni_integration.worker.avni.EnrolmentWorker;
import org.bahmni_avni_integration.worker.avni.SubjectWorker;
import org.bahmni_avni_integration.worker.bahmni.atomfeedworker.PatientEncounterEventWorker;
import org.bahmni_avni_integration.worker.bahmni.atomfeedworker.PatientEventWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ErrorRecordsWorker {
    @Autowired
    private ErrorRecordRepository errorRecordRepository;
    @Autowired
    private SubjectWorker subjectWorker;
    @Autowired
    private EnrolmentWorker enrolmentWorker;
    @Autowired
    private PatientEventWorker patientEventWorker;
    @Autowired
    private PatientEncounterEventWorker patientEncounterEventWorker;

    private static final Logger logger = Logger.getLogger(ErrorRecordsWorker.class);

    private static final int pageSize = 20;

    public void process() {
        Page<ErrorRecord> errorRecordPage;
        int pageNumber = 0;
        do {
            PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
            errorRecordPage = errorRecordRepository.findAllByErrorRecordLogsErrorTypeNotInOrderById(ErrorType.getUnprocessableErrorTypes(), pageRequest);
            pageNumber++;
            List<ErrorRecord> errorRecords = errorRecordPage.getContent();
            for (ErrorRecord errorRecord : errorRecords) {
                ErrorRecordWorker errorRecordWorker = getErrorRecordWorker(errorRecord);
                errorRecordWorker.processError(errorRecord.getEntityId());
            }
        } while (errorRecordPage.getNumberOfElements() == pageSize);
    }

    private ErrorRecordWorker getErrorRecordWorker(ErrorRecord errorRecord) {
        if (errorRecord.getAvniEntityType() != null) {
            if (errorRecord.getAvniEntityType().equals(AvniEntityType.Subject)) return subjectWorker;
            if (errorRecord.getAvniEntityType().equals(AvniEntityType.Enrolment)) return enrolmentWorker;
        } else if (errorRecord.getBahmniEntityType() != null) {
            if (errorRecord.getBahmniEntityType().equals(BahmniEntityType.Patient)) return patientEventWorker;
            if (errorRecord.getBahmniEntityType().equals(BahmniEntityType.Encounter)) return patientEncounterEventWorker;
        }
        throw new AssertionError(String.format("Invalid error record with AvniEntityType=%s and BahmniEntityType=%s", errorRecord.getAvniEntityType(), errorRecord.getBahmniEntityType()));
    }
}