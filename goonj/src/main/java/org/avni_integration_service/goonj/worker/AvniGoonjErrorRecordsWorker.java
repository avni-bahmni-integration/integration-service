package org.avni_integration_service.goonj.worker;

import org.apache.log4j.Logger;
import org.avni_integration_service.avni.SyncDirection;
import org.avni_integration_service.avni.worker.ErrorRecordWorker;
import org.avni_integration_service.goonj.GoonjEntityType;
import org.avni_integration_service.goonj.service.AvniGoonjErrorService;
import org.avni_integration_service.goonj.worker.avni.ActivityWorker;
import org.avni_integration_service.goonj.worker.avni.DispatchReceiptWorker;
import org.avni_integration_service.goonj.worker.avni.DistributionWorker;
import org.avni_integration_service.goonj.worker.goonj.DemandEventWorker;
import org.avni_integration_service.goonj.worker.goonj.DispatchEventWorker;
import org.avni_integration_service.integration_data.domain.error.ErrorRecord;
import org.avni_integration_service.integration_data.repository.ErrorRecordRepository;
import org.avni_integration_service.integration_data.repository.IntegrationSystemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AvniGoonjErrorRecordsWorker {
    @Autowired
    private ErrorRecordRepository errorRecordRepository;
    @Autowired
    private AvniGoonjErrorService avniGoonjErrorService;
    @Autowired
    private DemandEventWorker demandEventWorker;
    @Autowired
    private DispatchEventWorker dispatchEventWorker;
    @Autowired
    private DispatchReceiptWorker dispatchReceiptWorker;
    @Autowired
    private DistributionWorker distributionWorker;
    @Autowired
    private ActivityWorker activityWorker;
    @Autowired
    private IntegrationSystemRepository integrationSystemRepository;

    private static final Logger logger = Logger.getLogger(AvniGoonjErrorRecordsWorker.class);

    private static final int pageSize = 20;

    public void process(SyncDirection syncDirection, boolean allErrors) throws Exception {
        Page<ErrorRecord> errorRecordPage;
        int pageNumber = 0;
        do {
            logger.info(String.format("Starting page number: %d for sync direction: %s", pageNumber, syncDirection.name()));
            PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
            if (syncDirection.equals(SyncDirection.AvniToGoonj))
                errorRecordPage = errorRecordRepository.findAllByAvniEntityTypeNotNullAndProcessingDisabledFalseAndErrorRecordLogsErrorTypeNotInAndIntegrationSystemOrderById(
                        avniGoonjErrorService.getUnprocessableErrorTypes(), integrationSystemRepository.findByName("Goonj"), pageRequest);
            else if (syncDirection.equals(SyncDirection.GoonjToAvni) && !allErrors)
                errorRecordPage = errorRecordRepository.findAllByIntegratingEntityTypeNotNullAndProcessingDisabledFalseAndErrorRecordLogsErrorTypeNotInAndIntegrationSystemOrderById(
                        avniGoonjErrorService.getUnprocessableErrorTypes(), integrationSystemRepository.findByName("Goonj"), pageRequest);
            else if (syncDirection.equals(SyncDirection.GoonjToAvni) && allErrors)
                errorRecordPage = errorRecordRepository.findAllByIntegratingEntityTypeNotNullAndErrorRecordLogsErrorTypeNotInAndIntegrationSystemOrderById(
                        avniGoonjErrorService.getUnprocessableErrorTypes(), integrationSystemRepository.findByName("Goonj"), pageRequest);
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
        if (errorRecord.getIntegratingEntityType() != null) {
            if (errorRecord.getIntegratingEntityType().equals(GoonjEntityType.DispatchReceipt.name())) return dispatchReceiptWorker;
            if (errorRecord.getIntegratingEntityType().equals(GoonjEntityType.Distribution.name())) return distributionWorker;
            if (errorRecord.getIntegratingEntityType().equals(GoonjEntityType.Activity.name())) return activityWorker;
            if (errorRecord.getIntegratingEntityType().equals(GoonjEntityType.Demand.name())) return demandEventWorker;
            if (errorRecord.getIntegratingEntityType().equals(GoonjEntityType.Dispatch.name())) return dispatchEventWorker;
        }
        throw new AssertionError(String.format("Invalid error record with AvniEntityType=%s / GoonjEntityType=%s", errorRecord.getAvniEntityType(), errorRecord.getIntegratingEntityType()));
    }
}
