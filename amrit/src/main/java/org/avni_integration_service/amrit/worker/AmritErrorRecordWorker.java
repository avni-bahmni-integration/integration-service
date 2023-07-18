package org.avni_integration_service.amrit.worker;

import org.apache.log4j.Logger;
import org.avni_integration_service.amrit.config.AmritEntityType;
import org.avni_integration_service.amrit.service.AvniAmritErrorService;
import org.avni_integration_service.avni.SyncDirection;
import org.avni_integration_service.integration_data.domain.IntegrationSystem;
import org.avni_integration_service.integration_data.domain.error.ErrorRecord;
import org.avni_integration_service.integration_data.repository.ErrorRecordRepository;
import org.avni_integration_service.integration_data.repository.IntegrationSystemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AmritErrorRecordWorker {
    private static final Logger logger = Logger.getLogger(AmritErrorRecordWorker.class);
    private static final int pageSize = 20;
    @Autowired
    private ErrorRecordRepository errorRecordRepository;
    @Autowired
    private AvniAmritErrorService avniAmritErrorService;
    @Autowired
    private BeneficiaryWorker beneficiaryWorker;
    @Autowired
    private HouseholdWorker householdWorker;
    @Autowired
    private AmritEncounterWorker amritEncounterWorker;
    @Autowired
    private AmritEnrolmentWorker amritEnrolmentWorker;
    @Autowired
    private IntegrationSystemRepository integrationSystemRepository;

    public void processErrors(SyncDirection syncDirection, boolean allErrors) throws Exception {
        Page<ErrorRecord> errorRecordPage;
        int pageNumber = 0;
        do {
            logger.info(String.format("Starting page number: %d", pageNumber));
            PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
            Integer integrationSystemId = integrationSystemRepository.findBySystemType(IntegrationSystem.IntegrationSystemType.Amrit).getId();
            if (syncDirection.equals(SyncDirection.AvniToAmrit))
                errorRecordPage = errorRecordRepository.findAllByIntegratingEntityTypeNotNullAndErrorRecordLogsErrorTypeNotInAndIntegrationSystemIdOrderById(
                        avniAmritErrorService.getUnprocessableErrorTypes(),
                        integrationSystemId, pageRequest);
            else if (syncDirection.equals(SyncDirection.AvniToAmrit) && !allErrors)
                errorRecordPage = errorRecordRepository.findAllByIntegratingEntityTypeNotNullAndProcessingDisabledFalseAndErrorRecordLogsErrorTypeNotInAndIntegrationSystemIdOrderById(
                        avniAmritErrorService.getUnprocessableErrorTypes(), integrationSystemId, pageRequest);
            else
                throw new RuntimeException("Invalid arguments");

            List<ErrorRecord> errorRecords = errorRecordPage.getContent();
            for (ErrorRecord errorRecord : errorRecords) {
                ErrorRecordWorker errorRecordWorker = getErrorRecordWorker(errorRecord);
                errorRecordWorker.processError(errorRecord);
            }
            logger.info(String.format("Completed page number: %d with number of errors: %d", pageNumber, errorRecords.size()));
            pageNumber++;
        } while (errorRecordPage.getNumberOfElements() == pageSize);
    }

    private ErrorRecordWorker getErrorRecordWorker(ErrorRecord errorRecord) {
        if(errorRecord.getIntegratingEntityType() != null){
            if(errorRecord.getIntegratingEntityType().equals(AmritEntityType.Beneficiary.name())) return beneficiaryWorker;
            if(errorRecord.getIntegratingEntityType().equals(AmritEntityType.Household.name())) return householdWorker;
            if(errorRecord.getIntegratingEntityType().equals(AmritEntityType.BornBirth.name())) return amritEnrolmentWorker;
            if(errorRecord.getIntegratingEntityType().equals(AmritEntityType.CBAC.name())) return amritEncounterWorker;
        }
        throw new AssertionError(String.format("Invalid error record with AvniEntityType=%s / AmritEntityType=%s", errorRecord.getAvniEntityType(), errorRecord.getIntegratingEntityType()));
    }
}
