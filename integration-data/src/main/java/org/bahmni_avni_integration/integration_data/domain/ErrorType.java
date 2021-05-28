package org.bahmni_avni_integration.integration_data.domain;

import org.bahmni_avni_integration.integration_data.util.EnumUtil;

import java.util.Arrays;
import java.util.List;

public enum ErrorType implements BaseEnum {
    NoPatientWithId(1), PatientIdChanged(2), EntityIsDeleted(3), NotACommunityMember(4),
    NoSubjectWithId(5), SubjectIdChanged(6), MultipleSubjectsWithId(7), SubjectIdNull(8);

    private final int value;

    public static List<ErrorType> getUnprocessableErrorTypes() {
        return Arrays.asList(ErrorType.NotACommunityMember, ErrorType.EntityIsDeleted);
    }

    ErrorType(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }

    public static ErrorType findByValue(int value) {
        return (ErrorType) EnumUtil.findByValue(ErrorType.values(), value);
    }
}
