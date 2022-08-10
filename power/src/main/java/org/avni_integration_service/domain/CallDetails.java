package org.avni_integration_service.domain;

import org.avni_integration_service.avni.domain.Task;
import org.avni_integration_service.util.MapUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CallDetails {

    private static final String SID = "Sid";
    private static final String TO = "To";
    private static final String FROM = "From";

    private static final Map<String, StateProgram> stateToNumberMap = Map.of(
            "01141136600", new StateProgram("DL", "BoCW"),
            "01141132680", new StateProgram("DL", "RTE"),
            "01141132689", new StateProgram("CG", "RTE")
    );

    private Map<String, Object> response;

    public static CallDetails from(Map<String, Object> callResponse) {
        CallDetails callDetails = new CallDetails();
        callDetails.response = callResponse;
        return callDetails;
    }

    public Task createCallTask() {
        Task task = new Task();
        task.setTaskType("Call");
        task.setTaskStatus("New");
        task.setName("Call");
        task.setScheduledOn(new Date());
        StateProgram stateProgram = stateToNumberMap.get(MapUtil.getString(TO, response));
        if (stateProgram != null) {
            task.addMetadata("State", stateProgram.state);
            task.addMetadata("Program", stateProgram.program);
        }
        task.addMetadata("Number", MapUtil.getString(FROM, response));
        task.setObservations(new HashMap<>());
        task.setExternalId(MapUtil.getString(SID, response));
        return task;
    }

    private record StateProgram(String state, String program) {
    }
}
