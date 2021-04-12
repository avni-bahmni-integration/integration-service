package org.bahmni_avni_integration.repository.openmrs;

import org.bahmni_avni_integration.integration_data.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ImplementationConfigurationRepository {
    @Autowired
    private FileUtil fileUtil;

    public String getFirstRunPatientSql() {
        return fileUtil.readConfigFile("bahmni/first-run-patients.sql");
    }

    public String getFirstRunEncounterSql() {
        return fileUtil.readConfigFile("bahmni/first-run-encounters.sql");
    }
}