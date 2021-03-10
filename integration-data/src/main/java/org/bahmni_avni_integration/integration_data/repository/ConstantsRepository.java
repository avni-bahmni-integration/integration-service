package org.bahmni_avni_integration.integration_data.repository;

import org.bahmni_avni_integration.integration_data.domain.*;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConstantsRepository extends CrudRepository<Constant, Integer> {
    default Constants findAllConstants() {
        Iterable<Constant> all = findAll();
        return new Constants(all);
    }
}
