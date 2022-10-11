package org.avni_integration_service.integration_data.service.error;

import org.avni_integration_service.integration_data.domain.IntegrationSystem;
import org.avni_integration_service.integration_data.domain.error.ErrorType;
import org.avni_integration_service.integration_data.repository.ErrorTypeRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Classify error message into appropriate error type
 */
@Component
public class ErrorClassifier {
    private ErrorTypeRepository errorTypeRepository;

    public ErrorClassifier(ErrorTypeRepository errorTypeRepository) {
        this.errorTypeRepository = errorTypeRepository;
    }

    /**
     * Returns an instance of ErrorType, if any match with errorMsg.
     * Otherwise, returns null.
     * @param integrationSystem
     * @param errorMsg
     * @return
     */
    public ErrorType classify(IntegrationSystem integrationSystem, String errorMsg) {
        List<ErrorType> allErrorTypesByIntegrationSystem = errorTypeRepository.findAllByIntegrationSystem(integrationSystem);
        return allErrorTypesByIntegrationSystem.stream().filter(errType -> evaluate(errorMsg, errType))
                .findAny().orElse(null);
    }

    /**
     * Returns an instance of ErrorType, if any match with exception's message.
     * Otherwise, returns null.
     * @param integrationSystem
     * @param error
     * @return
     */
    public ErrorType classify(IntegrationSystem integrationSystem, @NonNull Exception error) {
        return classify(integrationSystem, error.getLocalizedMessage());
    }

    private boolean evaluate(String errorMsg, ErrorType errType) {
        if(!StringUtils.hasText(errorMsg) || errType == null
                || !StringUtils.hasText(errType.getComparisonValue())
                || errType.getComparisonOperator() == null) {
            return false;
        }
        switch (errType.getComparisonOperator()) {
            case EQUALS: return errorMsg.equalsIgnoreCase(errType.getComparisonValue());
            case CONTAINS: return errorMsg.toLowerCase().contains(errType.getComparisonValue().toLowerCase());
            case MATCHES: return Pattern.matches(errType.getComparisonValue(), errorMsg);
            default : return false;
        }
    }

}
