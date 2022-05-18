package org.avni_integration_service.bahmni.worker;

import org.apache.log4j.Logger;
import org.avni_integration_service.bahmni.BahmniEntityType;
import org.avni_integration_service.bahmni.SyncDirection;
import org.avni_integration_service.integration_data.domain.*;
import org.avni_integration_service.integration_data.domain.error.ErrorRecord;
import org.avni_integration_service.integration_data.domain.error.ErrorType;
import org.avni_integration_service.integration_data.repository.ErrorRecordRepository;
import org.avni_integration_service.bahmni.worker.avni.EnrolmentWorker;
import org.avni_integration_service.bahmni.worker.avni.GeneralEncounterWorker;
import org.avni_integration_service.bahmni.worker.avni.ProgramEncounterWorker;
import org.avni_integration_service.bahmni.worker.avni.SubjectWorker;
import org.avni_integration_service.bahmni.worker.bahmni.atomfeedworker.PatientEncounterEventWorker;
import org.avni_integration_service.bahmni.worker.bahmni.atomfeedworker.PatientEventWorker;
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
                errorRecordPage = errorRecordRepository.findAllByIntegratingEntityTypeNotNullAndProcessingDisabledFalseAndErrorRecordLogsErrorTypeNotInOrderById(ErrorType.getUnprocessableErrorTypes(), pageRequest);
            else if (syncDirection.equals(SyncDirection.BahmniToAvni) && allErrors)
                errorRecordPage = errorRecordRepository.findAllByIntegratingEntityTypeNotNullAndErrorRecordLogsErrorTypeNotInOrderById(ErrorType.getUnprocessableErrorTypes(), pageRequest);
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
        } else if (errorRecord.getIntegratingEntityType() != null) {
            if (errorRecord.getIntegratingEntityType().equals(BahmniEntityType.Patient)) return patientEventWorker;
            if (errorRecord.getIntegratingEntityType().equals(BahmniEntityType.Encounter)) return patientEncounterEventWorker;
        }
        throw new AssertionError(String.format("Invalid error record with AvniEntityType=%s and BahmniEntityType=%s", errorRecord.getAvniEntityType(), errorRecord.getIntegratingEntityType()));
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
