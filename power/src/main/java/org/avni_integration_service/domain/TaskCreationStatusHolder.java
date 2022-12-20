package org.avni_integration_service.domain;

import java.util.ArrayList;
import java.util.List;

public class TaskCreationStatusHolder {

    private final List<TaskCreationStatus> taskCreationStatuses = new ArrayList<>();

    public void addStatuses(List<TaskCreationStatus> statuses) {
        taskCreationStatuses.addAll(statuses);
    }

    private int total() {
        return taskCreationStatuses.size();
    }

    private long countByStatus(TaskCreationStatus taskCreationStatus) {
        return taskCreationStatuses.stream().filter(ts -> ts.equals(taskCreationStatus)).count();
    }

    public String getTaskCreationStatus() {
        return String.format("Finished processing the calls, Job details: Total processed: %d, newly created tasks: %d, skipped calls: %d, failed to process: %d",
                total(),
                countByStatus(TaskCreationStatus.Success),
                countByStatus(TaskCreationStatus.Skipped),
                countByStatus(TaskCreationStatus.Failure)
        );
    }

    public enum TaskCreationStatus {
        Success, Failure, Skipped
    }

}
