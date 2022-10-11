package org.avni_integration_service.integration_data.service.error;

import org.avni_integration_service.integration_data.domain.IntegrationSystem;
import org.avni_integration_service.integration_data.domain.error.ErrorType;
import org.avni_integration_service.integration_data.repository.AbstractSpringTest;
import org.avni_integration_service.integration_data.repository.IntegrationSystemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(classes = {ErrorClassifier.class, IntegrationSystemRepository.class})
public class ErrorClassifierForTest extends AbstractSpringTest implements ErrorClassifierForGoonjTestConstants {
        public static final String INT_SYSTEM_GOONJ = "Goonj";
        private final ErrorClassifier errorClassifier;
        private final IntegrationSystem integrationSystem;

        @Autowired
        public ErrorClassifierForTest(ErrorClassifier errorClassifier, IntegrationSystemRepository integrationSystemRepository) {
                this.errorClassifier = errorClassifier;
                this.integrationSystem = integrationSystemRepository.findByName(INT_SYSTEM_GOONJ);
        }

        @Test
        public void classifyMissingDemandError() {
                ErrorType classifiedErrorType = errorClassifier.classify(integrationSystem, ERROR_MSG_DISPATCH_MISSING_DEMAND);
                assertNotNull(classifiedErrorType);
        }

        @Test
        public void escapeMissingDemandError() {
                ErrorType classifiedErrorType = errorClassifier.classify(integrationSystem, ERROR_MSG_STANDARD_SKIP);
                assertNull(classifiedErrorType);
        }
}
