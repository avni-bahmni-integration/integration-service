package org.avni_integration_service.avni.domain;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Task extends AvniBaseContract {

    public static final String TASK_TYPE = "Task type";
    public static final String TASK_STATUS = "Task status";
    public static final String NAME = "Name";
    public static final String SCHEDULED_ON = "Scheduled on";
    public static final String COMPLETED_ON = "Completed on";
    public static final String ASSIGNED_TO = "Assigned to";
    public static final String METADATA = "metadata";
    public static final String EXTERNAL_ID = "External ID";

    public Task() {
        setMetadata(new HashMap<>());
    }

    public void setTaskType(String taskType) {
        set(TASK_TYPE, taskType);
    }

    public void setTaskStatus(String taskStatus) {
        set(TASK_STATUS, taskStatus);
    }

    public void setName(String name) {
        set(NAME, name);
    }

    public void setScheduledOn(Date scheduledOn) {
        set(SCHEDULED_ON, scheduledOn);
    }

    public void setExternalId(String externalId) {
        set(EXTERNAL_ID, externalId);
    }

    public void addMetadata(String conceptName, Object value) {
        Map<String, Object> map = getMetadata();
        map.put(conceptName, value);
    }

    public Map<String, Object> getMetadata() {
        Object metadata = get(METADATA);
        if (metadata == null) return new HashMap<>();
        return (Map<String, Object>) metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        set(METADATA, metadata);
    }
}
