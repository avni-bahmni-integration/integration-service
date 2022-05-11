package org.avni_integration_service.scheduler;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntegrationTaskTest {
    @Test
    public void getTasks() {
        List<IntegrationTask> tasks = IntegrationTask.getTasks("BahmniPatient");
        assertEquals(1, tasks.size());
        assertEquals(IntegrationTask.BahmniPatient, tasks.get(0));
    }
}
