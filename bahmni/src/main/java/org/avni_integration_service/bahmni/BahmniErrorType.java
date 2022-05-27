package org.avni_integration_service.bahmni;

public enum BahmniErrorType {
    NoPatientWithId, PatientIdChanged, EntityIsDeleted, NotACommunityMember,
    NoSubjectWithId, SubjectIdChanged, MultipleSubjectsWithId, SubjectIdNull;
}
