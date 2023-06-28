package org.avni_integration_service.goonj.repository;

import org.apache.log4j.Logger;
import org.avni_integration_service.avni.client.AvniHttpClient;
import org.avni_integration_service.avni.domain.GeneralEncounter;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.goonj.config.GoonjConfig;
import org.avni_integration_service.goonj.util.DateTimeUtil;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.avni_integration_service.util.ObjectJsonMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.*;

import java.net.URI;
import java.util.*;

public abstract class GoonjBaseRepository {
    private static final Logger logger = Logger.getLogger(GoonjBaseRepository.class);
    private static final String DELETION_RECORD_ID = "recordId";
    private static final String DELETION_SOURCE_ID = "sourceId";
    private final IntegratingEntityStatusRepository integratingEntityStatusRepository;
    private final RestTemplate goonjRestTemplate;
    protected final GoonjConfig goonjConfig;
    private final String entityType;
    protected final AvniHttpClient avniHttpClient;

    public GoonjBaseRepository(IntegratingEntityStatusRepository integratingEntityStatusRepository,
                               RestTemplate restTemplate, GoonjConfig goonjConfig, String entityType,
                               AvniHttpClient avniHttpClient) {
        this.integratingEntityStatusRepository = integratingEntityStatusRepository;
        this.goonjRestTemplate = restTemplate;
        this.goonjConfig = goonjConfig;
        this.entityType = entityType;
        this.avniHttpClient = avniHttpClient;
    }


    protected <T> T getResponseEntity(String resource, HashMap<String, String> queryParams, Class<T> returnType) {
        ResponseEntity<T> responseEntity = avniHttpClient.get(resource, queryParams, returnType);
        if(responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        }
        logger.error(String.format("Failed to fetch resource %s, response status code is %s", resource, responseEntity.getStatusCode()));
        throw new HttpServerErrorException(responseEntity.getStatusCode());

    }

    protected <T> T getResponse(Date dateTime, String resource,  Class<T> returnType, String dateTimeParam) {
        URI uri = URI.create(String.format("%s/%s?%s=%s", goonjConfig.getAppUrl(), resource,
                dateTimeParam, DateTimeUtil.formatDateTime(dateTime)));
        ResponseEntity<T> responseEntity = goonjRestTemplate.exchange(uri, HttpMethod.GET, null, returnType);
        if(responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        }
        logger.error(String.format("Failed to fetch data for resource %s, response status code is %s", resource, responseEntity.getStatusCode()));
        throw new HttpServerErrorException(responseEntity.getStatusCode());
    }

    /**
     * All our Sync time-stamps for Goonj, i.e. Demand, Dispatch, Distro, DispatchReceipt and Activity
     * are stored using integrating_entity_status DB and none in avniEntityStatus table.
     * @return cutOffDate
     */
    protected Date getCutOffDate() {
        return integratingEntityStatusRepository.findByEntityType(entityType).getReadUptoDateTime();
    }

    protected Date getCutOffDateTime() {
        return getCutOffDate();
    }

    protected <T> T getSingleEntityResponse(String resource, String filter, String uuid, Class<T> returnType) {
        URI uri = URI.create(String.format("%s/%s?%s=%s", goonjConfig.getAppUrl(), resource, filter, uuid));
        ResponseEntity<T> responseEntity = goonjRestTemplate.exchange(uri, HttpMethod.GET, null, returnType);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        }
        logger.error(String.format("Failed to fetch data for resource %s, response status code is %s", resource, responseEntity.getStatusCode()));
        throw getRestClientResponseException(responseEntity.getHeaders(), responseEntity.getStatusCode(), null);
    }

    protected HashMap<String, Object>[] createSingleEntity(String resource, HttpEntity<?> requestEntity) throws RestClientResponseException {
        logger.info("Request body:" + ObjectJsonMapper.writeValueAsString(requestEntity.getBody()));
        URI uri = URI.create(String.format("%s/%s", goonjConfig.getAppUrl(), resource));
        ParameterizedTypeReference<HashMap<String, Object>[]> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<HashMap<String, Object>[]> responseEntity = goonjRestTemplate.exchange(uri, HttpMethod.POST, requestEntity, responseType);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        }
        logger.error(String.format("Failed to create resource %s,  response status code is %s", resource, responseEntity.getStatusCode()));
        throw handleError(responseEntity, responseEntity.getStatusCode());
    }

    protected Object deleteSingleEntity(String resource, HttpEntity<?> requestEntity) throws RestClientResponseException {
        logger.info("Request body:" + ObjectJsonMapper.writeValueAsString(requestEntity.getBody()));
        URI uri = URI.create(String.format("%s/%s", goonjConfig.getAppUrl(), resource));
        ParameterizedTypeReference<Object> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<Object> responseEntity = goonjRestTemplate.exchange(uri, HttpMethod.POST, requestEntity, responseType);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        }
        logger.error(String.format("Failed to delete resource %s, response error message is %s", resource, responseEntity.getBody()));
        throw getRestClientResponseException(responseEntity.getHeaders(), responseEntity.getStatusCode(), (String) responseEntity.getBody());
    }

    protected RestClientException handleError(ResponseEntity<HashMap<String, Object>[]> responseEntity, HttpStatus statusCode) {
        HashMap<String, Object>[] responseBody = responseEntity.getBody();
        String message = (String) responseBody[0].get("message");
        return getRestClientResponseException(responseEntity.getHeaders(), statusCode, message);
    }

    private RestClientResponseException getRestClientResponseException(HttpHeaders headers, HttpStatus statusCode, String message) {
        return switch (statusCode.series()) {
            case CLIENT_ERROR -> HttpClientErrorException.create(message, statusCode, null, headers, null, null);
            case SERVER_ERROR -> HttpServerErrorException.create(message, statusCode, null, headers, null, null);
            default -> new UnknownHttpStatusCodeException(message, statusCode.value(), null, headers, null, null);
        };
    }

    private HttpEntity<Map<String, List>> getDeleteEncounterHttpRequestEntity(GeneralEncounter encounter) {
        Map<String, List> deleteRequest = Map.of(DELETION_RECORD_ID, new ArrayList(), DELETION_SOURCE_ID, Arrays.asList(encounter.getUuid()) );
        HttpEntity<Map<String, List>> requestEntity = new HttpEntity<>(deleteRequest);
        return requestEntity;
    }

    public abstract HashMap<String, Object>[] fetchEvents();

    public abstract List<String> fetchDeletionEvents();
    public abstract HashMap<String, Object>[] createEvent(Subject subject);
    public abstract HashMap<String, Object>[] createEvent(Subject subject, GeneralEncounter encounter);

    public boolean wasEventCreatedSuccessfully(HashMap<String, Object>[] response) {
        return (response != null && response[0].get("errorCode") == null);
    }

    public Object deleteEvent(String resourceType, GeneralEncounter encounter) {
        HttpEntity<Map<String, List>> requestEntity = getDeleteEncounterHttpRequestEntity(encounter);
        return deleteSingleEntity(resourceType, requestEntity);
    }

}
