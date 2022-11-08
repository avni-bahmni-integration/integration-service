package org.avni_integration_service.amrit.config;

public enum AmritEntityType {
    BENEFICIARY("beneficiary"),
    BORN_BIRTH("born birth"),
    HOUSEHOLD("household"),
    CBAC_FORM("cbac form");


    final String dbName;

    AmritEntityType(String dbName) {
        this.dbName = dbName;
    }

    public String getDbName() {
        return dbName;
    }
}
