package org.avni_integration_service.integration_data.service.error;

import org.avni_integration_service.integration_data.context.ContextIntegrationSystem;
import org.avni_integration_service.integration_data.domain.IntegrationSystem;
import org.avni_integration_service.integration_data.domain.error.ErrorType;
import org.avni_integration_service.integration_data.repository.ErrorTypeRepository;
import org.avni_integration_service.integration_data.repository.IntegrationSystemRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Classify error message into appropriate error type
 */
@Component
public class ErrorClassifier {
    private ErrorTypeRepository errorTypeRepository;
    private final IntegrationSystemRepository integrationSystemRepository;

    public ErrorClassifier(ErrorTypeRepository errorTypeRepository, IntegrationSystemRepository integrationSystemRepository) {
        this.errorTypeRepository = errorTypeRepository;
        this.integrationSystemRepository = integrationSystemRepository;
    }

    /**
     * Returns an instance of ErrorType, if any match with errorMsg.
     * Otherwise, returns null.
     *
     * @param integrationSystem
     * @param errorMsg
     * @return
     */
    public ErrorType classify(ContextIntegrationSystem integrationSystem, String errorMsg) {
        return classify(integrationSystem, errorMsg, false);
    }

    public ErrorType classify(IntegrationSystem integrationSystem, String errorMsg) {
        List<ErrorType> allErrorTypesByIntegrationSystem = errorTypeRepository.findAllByIntegrationSystem(integrationSystem);
        return allErrorTypesByIntegrationSystem.stream().filter(errType -> evaluate(errorMsg, errType))
                .findAny().orElse(null);
    }

    /**
     * Returns an instance of ErrorType, if any match with exception's message.
     * Otherwise, returns null.
     *
     * @param integrationSystem
     * @param error
     * @return
     */
    public ErrorType classify(ContextIntegrationSystem integrationSystem, @NonNull Exception error) {
        return classify(integrationSystem, error.getLocalizedMessage());
    }

    public ErrorType classify(ContextIntegrationSystem integrationSystem, @NonNull Exception error, Boolean bypassErrors) {
        return classify(integrationSystem, error.getLocalizedMessage(), bypassErrors);
    }

    public ErrorType classify(ContextIntegrationSystem integrationSystem, String errorMsg, Boolean bypassErrors) {
        List<ErrorType> allErrorTypesByIntegrationSystem = errorTypeRepository.findAllByIntegrationSystemId(integrationSystem.getId());
        return allErrorTypesByIntegrationSystem.stream().filter(errType -> evaluate(errorMsg, errType))
                .findAny().orElseGet(() -> {
                    if (bypassErrors) { return getFallbackRecord(); }
                    else { return null; }
                });
    }

    private boolean evaluate(String errorMsg, ErrorType errType) {
        if (!StringUtils.hasText(errorMsg) || errType == null
                || !StringUtils.hasText(errType.getComparisonValue())
                || errType.getComparisonOperator() == null) {
            return false;
        }
        switch (errType.getComparisonOperator()) {
            case EQUALS:
                return errorMsg.equalsIgnoreCase(errType.getComparisonValue());
            case CONTAINS:
                return errorMsg.toLowerCase().contains(errType.getComparisonValue().toLowerCase());
            case MATCHES:
                return Pattern.matches(errType.getComparisonValue(), errorMsg);
            default:
                return false;
        }
    }

    private ErrorType getFallbackRecord() {
        Optional<ErrorType> optionalErrorType = Optional
                .ofNullable(errorTypeRepository.findByNameAndIntegrationSystem("UnclassifiedError", integrationSystemRepository.findBySystemType(IntegrationSystem.IntegrationSystemType.Goonj)));
        return optionalErrorType.orElse(null);
    }

}
