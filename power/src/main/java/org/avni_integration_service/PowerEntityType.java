package org.avni_integration_service;

public enum PowerEntityType {
    CALL_DETAILS("Call Details");

    final String dbName;

    PowerEntityType(String dbName) {
        this.dbName = dbName;
    }

    public String getDbName() {
        return dbName;
    }
}
