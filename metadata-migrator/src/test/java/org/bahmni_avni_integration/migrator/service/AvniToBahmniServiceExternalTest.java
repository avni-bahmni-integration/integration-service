package org.bahmni_avni_integration.migrator.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.SQLException;

@SpringBootTest
class AvniToBahmniServiceExternalTest {
    @Autowired
    private AvniToBahmniService avniToBahmniService;

    @Test
    @Disabled("Enable and run this to create forms in Bahmni")
    public void migrateForms() throws SQLException {
        avniToBahmniService.migrateForms();
    }
}