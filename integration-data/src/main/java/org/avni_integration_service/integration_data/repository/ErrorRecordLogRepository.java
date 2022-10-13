package org.avni_integration_service.integration_data.repository;

import org.avni_integration_service.integration_data.domain.IntegrationSystem;
import org.avni_integration_service.integration_data.domain.error.ErrorRecordLog;
import org.avni_integration_service.integration_data.domain.error.ErrorType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface ErrorRecordLogRepository extends PagingAndSortingRepository<ErrorRecordLog, Integer> {
    Page<ErrorRecordLog> findAllByErrorRecordEntityIdContainsAndErrorRecordIntegrationSystem(String entityId, IntegrationSystem integrationSystem,
                                                                                             Pageable pageable);
    Page<ErrorRecordLog> findAllByErrorTypeAndErrorRecordIntegrationSystem(ErrorType errorType, IntegrationSystem integrationSystem,
                                                                           Pageable pageable);
    Page<ErrorRecordLog> findAllByErrorTypeAndErrorRecordEntityIdContainsAndErrorRecordIntegrationSystem(ErrorType errorType, String entityId,
                                                                                                         IntegrationSystem integrationSystem,
                                                                                                         Pageable pageable);
    Page<ErrorRecordLog> findAllByLoggedAtAfterAndLoggedAtBeforeAndErrorRecordIntegrationSystem(Date start, Date endDate,
                                                                                                IntegrationSystem integrationSystem,
                                                                                                Pageable pageable);
    Page<ErrorRecordLog> findAllByLoggedAtBeforeAndErrorRecordIntegrationSystem(Date endDate, IntegrationSystem integrationSystem,
                                                                                Pageable pageable);
    Page<ErrorRecordLog> findAllByLoggedAtAfterAndErrorRecordIntegrationSystem(Date start, IntegrationSystem integrationSystem, Pageable pageable);
    Page<ErrorRecordLog> findAllByErrorRecordIntegrationSystem(IntegrationSystem currentIntegrationSystem, Pageable pageable);
}
