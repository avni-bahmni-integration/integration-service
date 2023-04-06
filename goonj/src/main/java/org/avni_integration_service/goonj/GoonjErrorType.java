package org.avni_integration_service.goonj;

public enum GoonjErrorType {
    NoDemandWithId, NoDispatchWithId, NoImplementationInventoryWithId, DemandIdChanged, DispatchIdChanged,
    ImplementationInventoryIdChanged, EntityIsDeleted, NoSubjectWithId, SubjectIdChanged,
    MultipleSubjectsWithId, SubjectIdNull, ErroredAvniEncounter,
    DemandAttributesMismatch, DispatchAttributesMismatch, DispatchReceiptAttributesMismatch,
    DistributionAttributesMismatch, ActivityAttributesMismatch, ImplementationInventoryAttributesMismatch,
    DemandDeletionFailure, DispatchDeletionFailure, DispatchLineItemsDeletionFailure;
}
