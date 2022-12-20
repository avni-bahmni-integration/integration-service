package org.avni_integration_service.avni.repository;

import org.avni_integration_service.avni.client.AvniHttpClient;
import org.avni_integration_service.avni.domain.Task;
import org.avni_integration_service.avni.domain.TasksResponse;
import org.avni_integration_service.util.ObjectJsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AvniTaskRepository extends BaseAvniRepository {
    @Autowired
    private AvniHttpClient avniHttpClient;

    public Task create(Task task) {
        ResponseEntity<Task> responseEntity = avniHttpClient.post("/api/task", task, Task.class);
        return responseEntity.getBody();
    }

    public Task[] getTasks(String taskType, boolean isTerminalStatus, Map<String, Object> concepts) {
        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("type", taskType);
        queryParams.put("isTerminalStatus", String.valueOf(isTerminalStatus));
        queryParams.put("metadata", ObjectJsonMapper.writeValueAsString(concepts));
        ResponseEntity<TasksResponse> responseEntity = avniHttpClient.get("/api/tasks", queryParams, TasksResponse.class);
        return responseEntity.getBody().getContent();
    }
}
