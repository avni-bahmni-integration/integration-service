package org.avni_integration_service.bahmni.job;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class IntegrationTaskTest {
    @Test
    public void getTasks() {
        List<IntegrationTask> tasks = IntegrationTask.getTasks("BahmniPatient");
        Assertions.assertEquals(1, tasks.size());
        Assertions.assertEquals(IntegrationTask.BahmniPatient, tasks.get(0));
    }
}
