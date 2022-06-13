package org.avni_integration_service.integration_data.repository;

import org.avni_integration_service.integration_data.domain.MappingMetaData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = MappingMetaDataRepository.class)
class MappingMetaDataRepositoryTest extends AbstractSpringTest {
    @Autowired
    private MappingMetaDataRepository mappingMetaDataRepository;

    @Test
    public void loadAll() {
        Iterable<MappingMetaData> all = mappingMetaDataRepository.findAll();
        List<MappingMetaData> list = new ArrayList<>();
        all.forEach(list::add);
        assertEquals(1, list.size());
    }
}
