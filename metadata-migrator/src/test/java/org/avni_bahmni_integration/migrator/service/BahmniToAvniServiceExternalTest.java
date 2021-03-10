package org.bahmni_avni_integration.migrator.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.SQLException;

@SpringBootTest
class BahmniToAvniServiceExternalTest {
    @Autowired
    private BahmniToAvniService bahmniToAvniService;

    @Test
    public void createForms() throws SQLException {
        bahmniToAvniService.migrateForms();
    }
}