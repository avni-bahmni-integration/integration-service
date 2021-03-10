package org.bahmni_avni_integration.repository;

import org.bahmni_avni_integration.domain.*;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConstantsRepository extends CrudRepository<Constant, Integer> {
    default Constants findAllConstants() {
        Iterable<Constant> all = findAll();
        return new Constants(all);
    }
}
