package org.avni_integration_service.amrit.worker;

import org.apache.log4j.Logger;
import org.avni_integration_service.amrit.SyncDirection;
import org.avni_integration_service.amrit.config.AmritEntityType;
import org.avni_integration_service.amrit.config.AmritMappingDbConstants;
import org.avni_integration_service.amrit.service.AvniAmritErrorService;
import org.avni_integration_service.amrit.service.BeneficiaryService;
import org.avni_integration_service.integration_data.domain.error.ErrorRecord;
import org.avni_integration_service.integration_data.repository.ConstantsRepository;
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
    private BeneficiaryService beneficiaryService;
    @Autowired
    private BeneficiaryWorker beneficiaryWorker;
    @Autowired
    private IntegrationSystemRepository integrationSystemRepository;

    public void processErrors(SyncDirection syncDirection, boolean allErrors) throws Exception {
        Page<ErrorRecord> errorRecordPage;
        int pageNumber = 0;
        do {
            logger.info(String.format("Starting page number: %d", pageNumber));
            PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
            if (syncDirection.equals(SyncDirection.AvniToAmrit))
                errorRecordPage = errorRecordRepository.findAllByIntegratingEntityTypeNotNullAndErrorRecordLogsErrorTypeNotInAndIntegrationSystemOrderById(
                        avniAmritErrorService.getUnprocessableErrorTypes(),
                        integrationSystemRepository.findByName(AmritMappingDbConstants.IntSystemName), pageRequest);
            else if (syncDirection.equals(SyncDirection.AvniToAmrit) && !allErrors)
                errorRecordPage = errorRecordRepository.findAllByIntegratingEntityTypeNotNullAndProcessingDisabledFalseAndErrorRecordLogsErrorTypeNotInAndIntegrationSystemOrderById(
                        avniAmritErrorService.getUnprocessableErrorTypes(), integrationSystemRepository.findByName(AmritMappingDbConstants.IntSystemName), pageRequest);
            else
                throw new RuntimeException("Invalid arguments");

            List<ErrorRecord> errorRecords = errorRecordPage.getContent();
            for (ErrorRecord errorRecord : errorRecords) {
                ErrorRecordWorker errorRecordWorker = getErrorRecordWorker(errorRecord);
                errorRecordWorker.processError(errorRecord.getEntityId());
            }
            logger.info(String.format("Completed page number: %d with number of errors: %d", pageNumber, errorRecords.size()));
            pageNumber++;
        } while (errorRecordPage.getNumberOfElements() == pageSize);
    }

    private ErrorRecordWorker getErrorRecordWorker(ErrorRecord errorRecord) {
        if(errorRecord.getIntegratingEntityType() != null){
            if(errorRecord.getIntegratingEntityType().equals(AmritEntityType.Beneficiary.name())) return beneficiaryWorker;
        }
        throw new AssertionError(String.format("Invalid error record with AvniEntityType=%s / AmritEntityType=%s", errorRecord.getAvniEntityType(), errorRecord.getIntegratingEntityType()));

    }


}
