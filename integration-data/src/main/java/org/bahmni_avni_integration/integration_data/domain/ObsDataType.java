package org.bahmni_avni_integration.integration_data.domain;

import java.util.Map;

public enum ObsDataType {
    Coded, Date, Numeric, Text, Boolean, NA, DateTime;
    private static final Map<String, ObsDataType> bahmniToAvniExceptionsMap = Map.of(
            "N/A", NA,
            "Datetime", DateTime,
            "Boolean", Coded
    );

    public static ObsDataType parseAvniDataType(String dataType) {
        try {
            return valueOf(dataType);
        } catch (Exception e) {
            return null;
        }
    }

    public static ObsDataType getAvniDataType(String bahmniDataType) {
        return bahmniToAvniExceptionsMap.containsKey(bahmniDataType) ? bahmniToAvniExceptionsMap.get(bahmniDataType) : ObsDataType.valueOf(bahmniDataType);
    }
}
