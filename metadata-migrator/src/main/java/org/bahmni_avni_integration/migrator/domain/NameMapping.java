package org.bahmni_avni_integration.migrator.domain;

public class NameMapping {
    public static String fromBahmniToAvni(String name) {
        return String.format("%s [H]", name);
    }
}