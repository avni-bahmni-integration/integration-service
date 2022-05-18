package org.avni_integration_service;

import org.avni_integration_service.web.ErrorRecordLogController;
import org.avni_integration_service.web.TestController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ErrorRecordLogController.class)
class AvniIntegrationApplicationTests extends AbstractIntegrationTest {
    @Autowired
    private ErrorRecordLogController dummyBean;

	@Test
	void contextLoads() {
	}
}
