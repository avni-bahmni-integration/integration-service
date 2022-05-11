package org.avni_integration_service.repository.openmrs;

import org.avni_integration_service.integration_data.util.FileUtil;
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

    public String getLabEncounterSql() {
        return fileUtil.readConfigFile("bahmni/lab-results.sql");
    }
}
