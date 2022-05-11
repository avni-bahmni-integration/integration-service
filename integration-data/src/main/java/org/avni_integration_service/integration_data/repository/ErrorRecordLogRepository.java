package org.avni_integration_service.integration_data.repository;

import org.avni_integration_service.integration_data.domain.ErrorRecordLog;
import org.avni_integration_service.integration_data.domain.ErrorType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface ErrorRecordLogRepository extends PagingAndSortingRepository<ErrorRecordLog, Integer> {
    Page<ErrorRecordLog> findAllByErrorRecordEntityIdContains(String entityId, Pageable pageable);
    Page<ErrorRecordLog> findAllByErrorType(ErrorType errorType, Pageable pageable);
    Page<ErrorRecordLog> findAllByErrorTypeAndErrorRecordEntityIdContains(ErrorType errorType, String entityId, Pageable pageable);

    Page<ErrorRecordLog> findAllByLoggedAtAfterAndLoggedAtBefore(Date start, Date endDate, Pageable pageable);
    Page<ErrorRecordLog> findAllByLoggedAtBefore(Date endDate, Pageable pageable);
    Page<ErrorRecordLog> findAllByLoggedAtAfter(Date start, Pageable pageable);
}
