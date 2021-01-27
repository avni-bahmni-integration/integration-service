package org.bahmni_avni_integration.repository.openmrs;

import org.springframework.beans.factory.annotation.Value;

public abstract class BaseOpenMRSRepository {
    @Value("${openmrs.uri.prefix}")
    protected String urlPrefix;

    private static String OPENMRS_BASE_PATH = "/openmrs/ws/rest/v1/";

    protected String getResourcePath(String resource) {
        return String.format("%s%s%s/", urlPrefix, OPENMRS_BASE_PATH, resource).replace("//", "/");
    }
}