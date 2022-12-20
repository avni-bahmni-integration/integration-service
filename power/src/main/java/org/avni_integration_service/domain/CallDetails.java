package org.avni_integration_service.domain;

import org.avni_integration_service.avni.domain.Task;
import org.avni_integration_service.util.DateTimeUtil;
import org.avni_integration_service.util.MapUtil;

import java.util.HashMap;
import java.util.Map;

public class CallDetails {

    private static final String SID = "Sid";
    private static final String FROM = "From";
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
        task.setScheduledOn(DateTimeUtil.convertToDate((String) response.get("DateCreated")));
        task.addMetadata("Number", MapUtil.getString(FROM, response));
        task.setObservations(new HashMap<>());
        task.setExternalId(MapUtil.getString(SID, response));
        return task;
    }

}
