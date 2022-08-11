package org.avni_integration_service.worker;

import org.apache.log4j.Logger;
import org.avni_integration_service.PowerEntityType;
import org.avni_integration_service.PowerErrorType;
import org.avni_integration_service.dto.CallDTO;
import org.avni_integration_service.integration_data.domain.error.ErrorRecord;
import org.avni_integration_service.integration_data.repository.ErrorRecordRepository;
import org.avni_integration_service.service.AvniPowerErrorService;
import org.avni_integration_service.service.CallDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PowerErrorRecordWorker {
    private static final Logger logger = Logger.getLogger(PowerErrorRecordWorker.class);
    private static final int pageSize = 20;
    @Autowired
    private ErrorRecordRepository errorRecordRepository;
    @Autowired
    private AvniPowerErrorService avniPowerErrorService;
    @Autowired
    private CallDetailsService callDetailsService;
    @Autowired
    private CallDetailsWorker callDetailsWorker;

    public void processErrors() {
        Page<ErrorRecord> errorRecordPage;
        int pageNumber = 0;
        do {
            logger.info(String.format("Starting page number: %d", pageNumber));
            PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
            errorRecordPage = errorRecordRepository.findAllByIntegratingEntityTypeNotNullAndErrorRecordLogsErrorTypeNotInOrderById(avniPowerErrorService.getUnprocessableErrorTypes(), pageRequest);
            List<ErrorRecord> errorRecords = errorRecordPage.getContent();
            for (ErrorRecord errorRecord : errorRecords) {
                processError(errorRecord.getEntityId());
            }
            logger.info(String.format("Completed page number: %d with number of errors: %d", pageNumber, errorRecords.size()));
            pageNumber++;
        } while (errorRecordPage.getNumberOfElements() == pageSize);
    }


    public void processError(String sid) {
        CallDTO callDTO = callDetailsService.fetchCallBySID(sid);
        if (callDTO == null) {
            logger.warn(String.format("Call has been deleted now sid: %s", sid));
            avniPowerErrorService.errorOccurred(sid, PowerErrorType.CallSidDeleted, PowerEntityType.CALL_DETAILS);
            return;
        }
        callDetailsWorker.processCall(callDTO.getCall(), false);
    }

}
