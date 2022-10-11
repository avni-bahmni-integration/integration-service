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
        private final ErrorClassifier errorClassifier;
        private final IntegrationSystem integrationSystem;

        @Autowired
        public ErrorClassifierForTest(ErrorClassifier errorClassifier, IntegrationSystemRepository integrationSystemRepository) {
                this.errorClassifier = errorClassifier;
                this.integrationSystem = integrationSystemRepository.findByName(INT_SYSTEM_GOONJ);
        }

        @Test
        public void classifyMissingDemandError() {
                assertNotNull(errorClassifier.classify(integrationSystem, ERROR_MSG_DISPATCH_MISSING_DEMAND));
        }

        @Test
        public void escapeMissingDemandError() {
                assertNull(errorClassifier.classify(integrationSystem, ERROR_MSG_STANDARD_SKIP));
        }

        @Test
        public void classifyInvalidValueForPicklistError() {
                assertNotNull(invokeClassifyForContains(ERROR_MSG_INVALID_VALUE_FOR_RESTRICTED_PICKLIST));
        }

        @Test
        public void classifyDispatchReceiptDuplicateError() {
                assertNotNull(invokeClassifyForContains(ERROR_MSG_DISPATCH_RECEIPT_DUPLICATE));
        }

        @Test
        public void classifyFieldCustomValidationError() {
                assertNotNull(invokeClassifyForContains(ERROR_MSG_FIELD_CUSTOM_VALIDATION_EXCEPTION));
        }

        private ErrorType invokeClassifyForContains(String errorMessage) {
                String buffetedMsg = String.format(STRING_FORMAT_BUFFETED_ERROR_MSG, errorMessage);
                return errorClassifier.classify(integrationSystem, buffetedMsg);
        }
}
