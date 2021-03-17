package org.bahmni_avni_integration.integration_data.domain;

public enum ObsDataType {
    Coded, Date, Numeric, Text;

    public static ObsDataType parseAvniDataType(String dataType) {
        try {
            return valueOf(dataType);
        } catch (Exception e) {
            return null;
        }
    }
}
