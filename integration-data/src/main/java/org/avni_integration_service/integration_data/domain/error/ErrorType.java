package org.avni_integration_service.integration_data.domain.error;

import java.util.Arrays;
import java.util.List;

public enum ErrorType {
    NoIntEntityWithId(1), IntEntityIdChanged(2), EntityIsDeleted(3), NotAvniEntityFound(4),
    NoSubjectWithId(5), SubjectIdChanged(6), MultipleSubjectsWithId(7), SubjectIdNull(8);

    private final int value;

    public static List<ErrorType> getUnprocessableErrorTypes() {
        return Arrays.asList(ErrorType.NotAvniEntityFound, ErrorType.EntityIsDeleted);
    }

    ErrorType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ErrorType findByValue(int value) {
        return Arrays.stream(ErrorType.values()).filter(baseEnum -> baseEnum.getValue() == value).findFirst().orElse(null);
    }
}
