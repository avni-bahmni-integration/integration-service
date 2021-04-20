package org.bahmni_avni_integration.config;

import com.bugsnag.Bugsnag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BugsnagExternalTest {
    @Autowired
    private Bugsnag bugsnag;

    @Test
    public void testNotify() {
        bugsnag.notify(new RuntimeException("Test error"));
    }
}