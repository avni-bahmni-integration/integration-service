package org.avni_integration_service.goonj;

public enum GoonjErrorType {
    NoDemandWithId, NoDispatchWithId, DemandIdChanged, DispatchIdChanged, EntityIsDeleted,
    NoSubjectWithId, SubjectIdChanged, MultipleSubjectsWithId, SubjectIdNull, ErroredAvniEncounter,
    DemandAttributesMismatch, DispatchAttributesMismatch, DispatchReceiptAttributesMismatch,
    DistributionAttributesMismatch, ActivityAttributesMismatch,
    DemandDeletionFailure, DispatchDeletionFailure, DispatchLineItemsDeletionFailure;
}
