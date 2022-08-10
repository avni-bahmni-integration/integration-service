package org.avni_integration_service.worker;

import org.apache.log4j.Logger;
import org.avni_integration_service.PowerEntityType;
import org.avni_integration_service.avni.domain.Task;
import org.avni_integration_service.avni.repository.AvniTaskRepository;
import org.avni_integration_service.domain.CallDetails;
import org.avni_integration_service.dto.CallDetailsDTO;
import org.avni_integration_service.integration_data.domain.IntegratingEntityStatus;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.avni_integration_service.service.CallDetailsService;
import org.avni_integration_service.util.DateTimeUtil;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CallDetailsWorker {
    private static final Logger logger = Logger.getLogger(CallDetailsWorker.class);
    private final CallDetailsService callDetailsService;
    private final IntegratingEntityStatusRepository integratingEntityStatusRepository;
    private final AvniTaskRepository avniTaskRepository;

    public CallDetailsWorker(CallDetailsService callDetailsService,
                             IntegratingEntityStatusRepository integratingEntityStatusRepository,
                             AvniTaskRepository avniTaskRepository) {
        this.callDetailsService = callDetailsService;
        this.integratingEntityStatusRepository = integratingEntityStatusRepository;
        this.avniTaskRepository = avniTaskRepository;
    }

    public void fetchCallDetails() {
        CallDetailsDTO callDetailsDTO = callDetailsService.fetchBulkCallDetails();
        List<HashMap<String, Object>> newCalls = callDetailsDTO.getCalls();
        logger.info(String.format("Found %d newer calls", newCalls.size()));
        processCalls(newCalls);
        while (newCalls.size() > 0) {
            String nextPageUri = (String) callDetailsDTO.getMetadata().get("NextPageUri");
            callDetailsDTO = callDetailsService.fetchUsingNextPageURI(nextPageUri);
            newCalls = callDetailsDTO.getCalls();
            logger.info(String.format("Found %d newer calls", newCalls.size()));
            processCalls(newCalls);
        }
    }

    private void processCalls(List<HashMap<String, Object>> newCalls) {
        for (Map<String, Object> call : newCalls) {
            processCall(call);
        }
    }

    private void processCall(Map<String, Object> callResponse) {
        try {
            createTaskForCall(callResponse);
            updateReadUptoDateTime(callResponse);
        } catch (Exception e) {
            logger.error(String.format("Could not process the call details Sid %s", callResponse.get("Sid")), e);
        }
    }

    private void createTaskForCall(Map<String, Object> callResponse) {
        logger.debug(String.format("Processing call details Sid %s", callResponse.get("Sid")));
        CallDetails callDetails = CallDetails.from(callResponse);
        Task task = callDetails.createCallTask();
        avniTaskRepository.create(task);
    }

    <T> void updateReadUptoDateTime(Map<String, Object> callResponse) {
        IntegratingEntityStatus intEnt = integratingEntityStatusRepository.findByEntityType(PowerEntityType.CALL_DETAILS.getDbName());
        intEnt.setReadUptoDateTime(DateTimeUtil.convertToDate((String) callResponse.get("DateCreated")));
        integratingEntityStatusRepository.save(intEnt);
    }
}
