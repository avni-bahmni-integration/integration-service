package org.bahmni_avni_integration.repository;

import org.bahmni_avni_integration.domain.AvniEntityType;
import org.bahmni_avni_integration.domain.ErrorRecord;
import org.bahmni_avni_integration.domain.ErrorType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ErrorRecordRepository extends CrudRepository<ErrorRecord, Integer> {
    List<ErrorRecord> findAllByAvniEntityTypeAndSubjectPatientExternalId(AvniEntityType avniEntityType, String subjectPatientExternalId);
    ErrorRecord findByAvniEntityTypeAndSubjectPatientExternalIdAndErrorType(AvniEntityType avniEntityType, String subjectPatientExternalId, ErrorType errorType);
}
