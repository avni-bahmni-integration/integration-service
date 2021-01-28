package org.bahmni_avni_integration.repository;

public class MultipleResultsFoundException extends RuntimeException {
    public MultipleResultsFoundException() {
    }

    public MultipleResultsFoundException(String message) {
        super(message);
    }

    public MultipleResultsFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MultipleResultsFoundException(Throwable cause) {
        super(cause);
    }

    public MultipleResultsFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}