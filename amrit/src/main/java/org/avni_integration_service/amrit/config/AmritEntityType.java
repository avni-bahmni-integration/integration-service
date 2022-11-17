package org.avni_integration_service.amrit.config;

import org.avni_integration_service.integration_data.domain.framework.IntegrationEntityType;

public enum AmritEntityType implements IntegrationEntityType {
    Beneficiary("beneficiary"),
    BeneficiaryScan("beneficiary scan"),
    BornBirth("born birth"),
    Household("household"),
    CBACForm("cbac form");


    final String dbName;

    AmritEntityType(String dbName) {
        this.dbName = dbName;
    }

    public String getDbName() {
        return dbName;
    }
}
