package org.avni_integration_service.integration_data.repository;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

@NoRepositoryBean
public interface BaseRepository<T> extends PagingAndSortingRepository<T, Integer> {
    default T getEntity(int id) {
        return this.findById(id).get();
    }

    default T findEntity(int id) {
        return this.getEntity(id);
    }

    List<T> findByIdIn(Integer[] ids);
}
