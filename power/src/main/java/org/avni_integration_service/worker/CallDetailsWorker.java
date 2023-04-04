package org.avni_integration_service.worker;

import org.apache.log4j.Logger;
import org.avni_integration_service.avni.domain.Task;
import org.avni_integration_service.avni.repository.AvniTaskRepository;
import org.avni_integration_service.config.PowerEntityType;
import org.avni_integration_service.config.PowerErrorType;
import org.avni_integration_service.domain.CallDetails;
import org.avni_integration_service.domain.TaskCreationStatusHolder;
import org.avni_integration_service.domain.TaskCreationStatusHolder.TaskCreationStatus;
import org.avni_integration_service.dto.CallDetailsDTO;
import org.avni_integration_service.integration_data.domain.IntegratingEntityStatus;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.avni_integration_service.service.AvniPowerErrorService;
import org.avni_integration_service.service.CallDetailsService;
import org.avni_integration_service.service.PowerMappingMetadataService;
import org.avni_integration_service.util.DateTimeUtil;
import org.avni_integration_service.util.MapUtil;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CallDetailsWorker {
    private static final Logger logger = Logger.getLogger(CallDetailsWorker.class);
    private final CallDetailsService callDetailsService;
    private final IntegratingEntityStatusRepository integratingEntityStatusRepository;
    private final AvniTaskRepository avniTaskRepository;
    private final AvniPowerErrorService avniPowerErrorService;
    private final PowerMappingMetadataService powerMappingMetadataService;

    public CallDetailsWorker(CallDetailsService callDetailsService,
                             IntegratingEntityStatusRepository integratingEntityStatusRepository,
                             AvniTaskRepository avniTaskRepository, AvniPowerErrorService avniPowerErrorService,
                             PowerMappingMetadataService powerMappingMetadataService) {
        this.callDetailsService = callDetailsService;
        this.integratingEntityStatusRepository = integratingEntityStatusRepository;
        this.avniTaskRepository = avniTaskRepository;
        this.avniPowerErrorService = avniPowerErrorService;
        this.powerMappingMetadataService = powerMappingMetadataService;
    }

    public void fetchCallDetails() {
        Set<String> allCallPhoneNumbers = powerMappingMetadataService.findAllCallPhoneNumbers();
        for (String phoneNumber : allCallPhoneNumbers) {
            String state = powerMappingMetadataService.getStateValueForMobileNumber(phoneNumber);
            String program = powerMappingMetadataService.getProgramValueForMobileNumber(phoneNumber);
            TaskCreationStatusHolder taskCreationStatusHolder = new TaskCreationStatusHolder(phoneNumber);
            CallDetailsDTO callDetailsDTO = callDetailsService.fetchBulkCallDetails(phoneNumber);
            List<HashMap<String, Object>> newCalls = callDetailsDTO.getCalls();
            logger.info(String.format("Found %d newer calls for %s phoneNumber", newCalls.size(), phoneNumber));
            taskCreationStatusHolder.addStatuses(processCalls(newCalls, state, program));
            while (newCalls.size() > 0) {
                String nextPageUri = (String) callDetailsDTO.getMetadata().get("NextPageUri");
                callDetailsDTO = callDetailsService.fetchUsingNextPageURI(nextPageUri);
                newCalls = callDetailsDTO.getCalls();
                logger.info(String.format("Found %d newer calls for %s phoneNumber", newCalls.size(), phoneNumber));
                taskCreationStatusHolder.addStatuses(processCalls(newCalls, state, program));
            }
            logger.info(taskCreationStatusHolder.getTaskCreationStatus());
        }
    }

    private List<TaskCreationStatus> processCalls(List<HashMap<String, Object>> newCalls, String state, String program) {
        List<TaskCreationStatus> statuses = new ArrayList<>();
        for (Map<String, Object> call : newCalls) {
            TaskCreationStatus status = processCall(call, true, state, program);
            statuses.add(status);
        }
        return statuses;
    }

    public TaskCreationStatus processCall(Map<String, Object> callResponse, boolean updateSyncStatus,
                                          String state, String program) {
        String sid = (String) callResponse.get("Sid");
        try {
            logger.debug(String.format("Processing call details Sid %s", sid));
            if (isTaskExistsForCallInAvni(callResponse)) {
                logger.debug(String.format("Skipping task creation. Task with Number %s already exists", callResponse.get("From")));
                updateErrorRecordAndSyncStatus(callResponse, updateSyncStatus, sid);
                return TaskCreationStatus.Skipped;
            } else {
                createTaskForCall(callResponse, state, program);
                updateErrorRecordAndSyncStatus(callResponse, updateSyncStatus, sid);
                return TaskCreationStatus.Success;
            }
        } catch (Exception e) {
            logger.error(String.format("Could not process the call details Sid %s", sid), e);
            avniPowerErrorService.errorOccurred(sid, PowerErrorType.TaskNotSaved, PowerEntityType.CALL_DETAILS);
            updateSyncStatus(callResponse, updateSyncStatus);
            return TaskCreationStatus.Failure;
        }
    }

    private void updateErrorRecordAndSyncStatus(Map<String, Object> callResponse, boolean updateSyncStatus, String sid) {
        avniPowerErrorService.successfullyProcessed(sid, PowerEntityType.CALL_DETAILS);
        updateSyncStatus(callResponse, updateSyncStatus);
    }

    private void updateSyncStatus(Map<String, Object> callResponse, boolean updateSyncStatus) {
        if (updateSyncStatus) {
            updateReadUptoDateTime(callResponse);
        }
    }

    private void createTaskForCall(Map<String, Object> callResponse, String state, String program) {
        CallDetails callDetails = CallDetails.from(callResponse);
        Task task = callDetails.createCallTask();
        powerMappingMetadataService.addStateAndProgramToTaskMetadata(task, state, program);
        avniTaskRepository.create(task);
    }

    private boolean isTaskExistsForCallInAvni(Map<String, Object> callResponse) {
        Object number = callResponse.get("From");
        Task[] tasks = avniTaskRepository.getTasks("Call", false, Map.of("Number", number));
        return tasks.length > 0;
    }

    <T> void updateReadUptoDateTime(Map<String, Object> callResponse) {
        String phoneNumber = MapUtil.getString("PhoneNumber", callResponse);
        IntegratingEntityStatus intEnt = callDetailsService.getIntegratingEntityStatus(phoneNumber);
        intEnt.setReadUptoDateTime(DateTimeUtil.convertToDate((String) callResponse.get("DateCreated")));
        integratingEntityStatusRepository.save(intEnt);
    }
}
