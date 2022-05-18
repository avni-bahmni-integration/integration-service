package org.avni_integration_service.integration_data.repository;

import org.avni_integration_service.integration_data.domain.AvniEntityType;
import org.avni_integration_service.integration_data.domain.error.ErrorRecord;
import org.avni_integration_service.integration_data.domain.error.ErrorType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ErrorRecordRepository extends PagingAndSortingRepository<ErrorRecord, Integer> {
    ErrorRecord findByAvniEntityTypeAndEntityId(AvniEntityType avniEntityType, String entityId);
    ErrorRecord findByIntegratingEntityTypeAndEntityId(String integratingEntityType, String entityId);

    Page<ErrorRecord> findAllByAvniEntityTypeNotNullAndProcessingDisabledFalseAndErrorRecordLogsErrorTypeNotInOrderById(List<ErrorType> errorTypes, Pageable pageable);
    Page<ErrorRecord> findAllByIntegratingEntityTypeNotNullAndProcessingDisabledFalseAndErrorRecordLogsErrorTypeNotInOrderById(List<ErrorType> errorTypes, Pageable pageable);
    Page<ErrorRecord> findAllByIntegratingEntityTypeNotNullAndErrorRecordLogsErrorTypeNotInOrderById(List<ErrorType> errorTypes, Pageable pageable);

    List<ErrorRecord> findAllByAvniEntityTypeNotNull();
}
