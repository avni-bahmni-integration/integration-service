package org.bahmni_avni_integration.integration_data.domain;

import java.util.Arrays;
import java.util.List;

public enum ErrorType {
    NoPatientWithId, PatientIdChanged, EntityIsDeleted, NotACommunityMember,
    NoSubjectWithId, SubjectIdChanged, MultipleSubjectsWithId;

    public static List<ErrorType> getUnprocessableErrorTypes() {
        return Arrays.asList(ErrorType.NotACommunityMember, ErrorType.EntityIsDeleted);
    }
}
