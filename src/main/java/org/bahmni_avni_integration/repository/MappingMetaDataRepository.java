package org.bahmni_avni_integration.repository;

import org.bahmni_avni_integration.domain.MappingMetaData;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MappingMetaDataRepository extends PagingAndSortingRepository<MappingMetaData, Integer> {
}
