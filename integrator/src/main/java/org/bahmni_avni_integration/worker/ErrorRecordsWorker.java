package org.bahmni_avni_integration.worker;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.integration_data.BahmniEntityType;
import org.bahmni_avni_integration.integration_data.domain.*;
import org.bahmni_avni_integration.integration_data.repository.ErrorRecordRepository;
import org.bahmni_avni_integration.worker.avni.EnrolmentWorker;
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
    private PatientEventWorker patientEventWorker;
    @Autowired
    private PatientEncounterEventWorker patientEncounterEventWorker;

    private static final Logger logger = Logger.getLogger(ErrorRecordsWorker.class);

    private static final int pageSize = 20;

    public void process(SyncDirection syncDirection) {
        Page<ErrorRecord> errorRecordPage;
        int pageNumber = 0;
        do {
            logger.info(String.format("Starting page number: %d for sync direction: %s", pageNumber, syncDirection.name()));
            PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
            if (syncDirection.equals(SyncDirection.AvniToBahmni))
                errorRecordPage = errorRecordRepository.findAllByAvniEntityTypeNotNullAndErrorRecordLogsErrorTypeNotInOrderById(ErrorType.getUnprocessableErrorTypes(), pageRequest);
            else
                errorRecordPage = errorRecordRepository.findAllByBahmniEntityTypeNotNullAndErrorRecordLogsErrorTypeNotInOrderById(ErrorType.getUnprocessableErrorTypes(), pageRequest);

            pageNumber++;
            List<ErrorRecord> errorRecords = errorRecordPage.getContent();
            for (ErrorRecord errorRecord : errorRecords) {
                ErrorRecordWorker errorRecordWorker = getErrorRecordWorker(errorRecord);
                errorRecordWorker.processError(errorRecord.getEntityId());
            }
            logger.info(String.format("Completed page number: %d for sync direction: %s", pageNumber, syncDirection.name()));
        } while (errorRecordPage.getNumberOfElements() == pageSize);
    }

    private ErrorRecordWorker getErrorRecordWorker(ErrorRecord errorRecord) {
        if (errorRecord.getAvniEntityType() != null) {
            if (errorRecord.getAvniEntityType().equals(AvniEntityType.Subject)) return subjectWorker;
            if (errorRecord.getAvniEntityType().equals(AvniEntityType.Enrolment)) return enrolmentWorker;
            if (errorRecord.getAvniEntityType().equals(AvniEntityType.ProgramEncounter)) return programEncounterWorker;
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
        patientEventWorker.cacheRunImmutables(constants);
        patientEncounterEventWorker.cacheRunImmutables(constants);
    }
}