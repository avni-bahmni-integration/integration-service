package org.avni_integration_service.integration_data.repository;

import org.avni_integration_service.integration_data.domain.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends BaseRepository<User> {
    User findByEmail(String email);
    List<User> findAllBy();
}
