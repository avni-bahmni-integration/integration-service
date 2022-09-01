package org.avni_integration_service.goonj;

import org.avni_integration_service.integration_data.domain.framework.IntegrationEntityType;

public enum GoonjEntityType implements IntegrationEntityType {
    Demand("Demand"), Dispatch("Dispatch"), DispatchLineItem("Dispatch line item"),
    Activity("Activity"), DispatchReceipt("Dispatch receipt"), Distribution( "Distribution");

    String dbName;

    GoonjEntityType(String dbName) {
        this.dbName = dbName;
    }

    public String getDbName() {
        return dbName;
    }
}
