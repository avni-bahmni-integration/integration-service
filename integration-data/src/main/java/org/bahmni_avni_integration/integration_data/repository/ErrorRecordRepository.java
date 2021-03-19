package org.bahmni_avni_integration.integration_data.repository;

import org.bahmni_avni_integration.integration_data.BahmniEntityType;
import org.bahmni_avni_integration.integration_data.domain.AvniEntityType;
import org.bahmni_avni_integration.integration_data.domain.ErrorRecord;
import org.bahmni_avni_integration.integration_data.domain.ErrorType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ErrorRecordRepository extends CrudRepository<ErrorRecord, Integer> {
    List<ErrorRecord> findAllByAvniEntityTypeAndSubjectPatientExternalId(AvniEntityType avniEntityType, String subjectPatientExternalId);
    ErrorRecord findByAvniEntityTypeAndSubjectPatientExternalIdAndErrorType(AvniEntityType avniEntityType, String subjectPatientExternalId, ErrorType errorType);
    ErrorRecord findByAvniEntityTypeAndEnrolmentExternalIdAndErrorType(AvniEntityType avniEntityType, String subjectPatientExternalId, ErrorType errorType);
    ErrorRecord findByBahmniEntityTypeAndSubjectPatientExternalIdAndErrorType(BahmniEntityType bahmniEntityType, String subjectPatientExternalId, ErrorType errorType);

    List<ErrorRecord> findAllByAvniEntityTypeAndEnrolmentExternalId(AvniEntityType avniEntityType, String enrolmentExternalId);

    ErrorRecord findByBahmniEntityTypeAndEncounterExternalIdAndErrorType(BahmniEntityType bahmniEntityType, String existingEncounterUuid, ErrorType errorType);
}
