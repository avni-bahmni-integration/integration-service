package org.avni_integration_service.avni.repository;

import org.avni_integration_service.avni.client.AvniHttpClient;
import org.avni_integration_service.avni.domain.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class AvniTaskRepository extends BaseAvniRepository {
    @Autowired
    private AvniHttpClient avniHttpClient;

    public Task create(Task task) {
        ResponseEntity<Task> responseEntity = avniHttpClient.post("/api/task", task, Task.class);
        return responseEntity.getBody();
    }
}
