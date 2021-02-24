package org.bahmni_avni_integration.repository.openmrs;

import org.bahmni_avni_integration.client.OpenMRSWebClient;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSPatient;
import org.bahmni_avni_integration.contract.bahmni.SearchResults;
import org.bahmni_avni_integration.repository.MultipleResultsFoundException;
import org.bahmni_avni_integration.util.ObjectJsonMapper;
import org.ict4h.atomfeed.client.domain.Event;
import org.springframework.beans.factory.annotation.Value;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public abstract class BaseOpenMRSRepository {
    @Value("${openmrs.uri.prefix}")
    protected String urlPrefix;

    private static String OPENMRS_BASE_PATH = "openmrs/ws/rest/v1";

    protected OpenMRSWebClient openMRSWebClient;

    protected BaseOpenMRSRepository(OpenMRSWebClient openMRSWebClient) {
        this.openMRSWebClient = openMRSWebClient;
    }

    protected String getFullPath(String urlPart) {
        try {
            return String.format("%s/%s/%s", urlPrefix, OPENMRS_BASE_PATH, urlPart);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected String getResourcePath(String resource) {
        return getFullPath(resource);
    }

    protected String getSingleResourcePath(String resource, String uuid) {
        return String.format("%s/%s", getResourcePath(resource), uuid);
    }

    protected String encode(String x) {
        try {
            return URLEncoder.encode(x, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    protected <T> T pickAndExpectOne(SearchResults<T> searchResults, String searchParam) {
        if (searchResults.getResults().size() == 0) return null;
        if (searchResults.getResults().size() > 1) throw new MultipleResultsFoundException(String.format("More than one entity found with name: %s", searchParam));
        return searchResults.getResults().get(0);
    }

    protected <T> T pickOne(SearchResults<T> searchResults, String searchParam) {
        if (searchResults.getResults().size() == 0) return null;
        return searchResults.getResults().get(0);
    }

    protected String getUnderlyingResourceJson(Event event) {
        String content = event.getContent();
        return openMRSWebClient.get(URI.create(urlPrefix + content));
    }
}