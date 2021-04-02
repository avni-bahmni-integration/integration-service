package org.bahmni_avni_integration.integration_data.domain;

public enum ErrorType {
    NoPatientWithId, PatientIdChanged, PatientIsDeleted, NotACommunityMember,
    NoSubjectWithId, SubjectIdChanged, MultipleSubjectsWithId, NoSubjectWithExternalId
}
