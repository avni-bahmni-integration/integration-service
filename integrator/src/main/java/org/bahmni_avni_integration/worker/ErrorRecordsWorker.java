package org.bahmni_avni_integration.worker;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.integration_data.BahmniEntityType;
import org.bahmni_avni_integration.integration_data.domain.*;
import org.bahmni_avni_integration.integration_data.repository.ErrorRecordRepository;
import org.bahmni_avni_integration.worker.avni.EnrolmentWorker;
import org.bahmni_avni_integration.worker.avni.GeneralEncounterWorker;
import org.bahmni_avni_integration.worker.avni.ProgramEncounterWorker;
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
    private ProgramEncounterWorker programEncounterWorker;
    @Autowired
    private GeneralEncounterWorker generalEncounterWorker;
    @Autowired
    private PatientEventWorker patientEventWorker;
    @Autowired
    private PatientEncounterEventWorker patientEncounterEventWorker;

    private static final Logger logger = Logger.getLogger(ErrorRecordsWorker.class);

    private static final int pageSize = 20;

    public void process(SyncDirection syncDirection, boolean allErrors) {
        Page<ErrorRecord> errorRecordPage;
        int pageNumber = 0;
        do {
            logger.info(String.format("Starting page number: %d for sync direction: %s", pageNumber, syncDirection.name()));
            PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
            if (syncDirection.equals(SyncDirection.AvniToBahmni))
                errorRecordPage = errorRecordRepository.findAllByAvniEntityTypeNotNullAndProcessingDisabledFalseAndErrorRecordLogsErrorTypeNotInOrderById(ErrorType.getUnprocessableErrorTypes(), pageRequest);
            else if (syncDirection.equals(SyncDirection.BahmniToAvni) && !allErrors)
                errorRecordPage = errorRecordRepository.findAllByBahmniEntityTypeNotNullAndProcessingDisabledFalseAndErrorRecordLogsErrorTypeNotInOrderById(ErrorType.getUnprocessableErrorTypes(), pageRequest);
            else if (syncDirection.equals(SyncDirection.BahmniToAvni) && allErrors)
                errorRecordPage = errorRecordRepository.findAllByBahmniEntityTypeNotNullAndErrorRecordLogsErrorTypeNotInOrderById(ErrorType.getUnprocessableErrorTypes(), pageRequest);
            else
                throw new RuntimeException("Invalid arguments");

            List<ErrorRecord> errorRecords = errorRecordPage.getContent();
            for (ErrorRecord errorRecord : errorRecords) {
                ErrorRecordWorker errorRecordWorker = getErrorRecordWorker(errorRecord);
                errorRecordWorker.processError(errorRecord.getEntityId());
            }
            logger.info(String.format("Completed page number: %d with number of errors: %d, for sync direction: %s", pageNumber, errorRecords.size(), syncDirection.name()));
            pageNumber++;
        } while (errorRecordPage.getNumberOfElements() == pageSize);
    }

    private ErrorRecordWorker getErrorRecordWorker(ErrorRecord errorRecord) {
        if (errorRecord.getAvniEntityType() != null) {
            if (errorRecord.getAvniEntityType().equals(AvniEntityType.Subject)) return subjectWorker;
            if (errorRecord.getAvniEntityType().equals(AvniEntityType.Enrolment)) return enrolmentWorker;
            if (errorRecord.getAvniEntityType().equals(AvniEntityType.ProgramEncounter)) return programEncounterWorker;
            if (errorRecord.getAvniEntityType().equals(AvniEntityType.GeneralEncounter)) return generalEncounterWorker;
        } else if (errorRecord.getBahmniEntityType() != null) {
            if (errorRecord.getBahmniEntityType().equals(BahmniEntityType.Patient)) return patientEventWorker;
            if (errorRecord.getBahmniEntityType().equals(BahmniEntityType.Encounter)) return patientEncounterEventWorker;
        }
        throw new AssertionError(String.format("Invalid error record with AvniEntityType=%s and BahmniEntityType=%s", errorRecord.getAvniEntityType(), errorRecord.getBahmniEntityType()));
    }

    public void cacheRunImmutables(Constants constants) {
        subjectWorker.cacheRunImmutables(constants);
        enrolmentWorker.cacheRunImmutables(constants);
        programEncounterWorker.cacheRunImmutables(constants);
        generalEncounterWorker.cacheRunImmutables(constants);
        patientEventWorker.cacheRunImmutables(constants);
        patientEncounterEventWorker.cacheRunImmutables(constants);
    }
}
