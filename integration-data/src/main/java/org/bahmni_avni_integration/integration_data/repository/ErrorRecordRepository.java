package org.bahmni_avni_integration.integration_data.repository;

import org.bahmni_avni_integration.integration_data.BahmniEntityType;
import org.bahmni_avni_integration.integration_data.domain.AvniEntityType;
import org.bahmni_avni_integration.integration_data.domain.ErrorRecord;
import org.bahmni_avni_integration.integration_data.domain.ErrorType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ErrorRecordRepository extends PagingAndSortingRepository<ErrorRecord, Integer> {
    ErrorRecord findByAvniEntityTypeAndEntityId(AvniEntityType avniEntityType, String entityId);
    ErrorRecord findByBahmniEntityTypeAndEntityId(BahmniEntityType bahmniEntityType, String entityId);

    Page<ErrorRecord> findAllByErrorRecordLogsErrorTypeIn(List<ErrorType> errorTypes, Pageable pageable);
}