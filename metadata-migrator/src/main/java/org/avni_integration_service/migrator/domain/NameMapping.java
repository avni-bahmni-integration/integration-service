package org.avni_integration_service.migrator.domain;

public class NameMapping {
    public static final String BAHMNI_CONCEPT_SUFFIX = "[H]";
    public static final String AVNI_CONCEPT_SUFFIX = "[Avni]";
    public static final String BAHMNI_PERSON_ATTRIBUTE_SUFFIX = "[HP]";
    public static final String BAHMNI_SUFFIX = "[BAHMNI]";
    public static String fromBahmniConceptToAvni(String name) {
        return String.format("%s %s", name, BAHMNI_CONCEPT_SUFFIX);
    }

    public static String fromAvniNameToBahmni(String name) {
        return String.format("%s %s", name, AVNI_CONCEPT_SUFFIX);
    }

    public static String fromBahmniPersonAttributeToAvni(String name) {
        return String.format("%s %s", name, BAHMNI_PERSON_ATTRIBUTE_SUFFIX);
    }
}
