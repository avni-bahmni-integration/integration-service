package org.bahmni_avni_integration.repository;

import org.bahmni_avni_integration.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    User findByEmail(String email);
    List<User> findAllBy();
}
