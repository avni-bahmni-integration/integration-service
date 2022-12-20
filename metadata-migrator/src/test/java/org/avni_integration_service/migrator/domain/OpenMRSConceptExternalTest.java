package org.avni_integration_service.migrator.domain;

import org.avni_integration_service.migrator.repository.OpenMRSRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@Disabled
public class OpenMRSConceptExternalTest {
    @Autowired
    private OpenMRSRepository openMRSRepository;

    @Test
    public void biggerList() throws SQLException {
        List<OpenMRSConcept> concepts = openMRSRepository.getConcepts();
        List<OpenMRSConcept> conceptList1 = concepts.stream().filter(openMRSConcept -> openMRSConcept.getName().equals("Chest Pain")).toList();

    }
}
