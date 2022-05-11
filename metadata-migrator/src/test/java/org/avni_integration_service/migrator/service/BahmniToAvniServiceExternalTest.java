package org.avni_integration_service.migrator.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.SQLException;

@SpringBootTest
@Disabled
class BahmniToAvniServiceExternalTest {
    @Autowired
    private BahmniToAvniService bahmniToAvniService;

    @Test
    public void createForms() throws SQLException {
        bahmniToAvniService.migrateForms();
    }
}
