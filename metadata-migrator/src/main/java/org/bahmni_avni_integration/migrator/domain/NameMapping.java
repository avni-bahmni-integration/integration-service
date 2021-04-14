package org.bahmni_avni_integration.migrator.domain;

public class NameMapping {
    public static String fromBahmniConceptToAvni(String name) {
        return String.format("%s [H]", name);
    }

    public static String fromAvniNameToBahmni(String name) {
        return String.format("%s [Avni]", name);
    }

    public static String fromBahmniPersonAttributeToAvni(String name) {
        return String.format("%s [HP]", name);
    }
}