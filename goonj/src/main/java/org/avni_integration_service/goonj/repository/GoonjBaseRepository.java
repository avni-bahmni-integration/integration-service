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
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public abstract class GoonjBaseRepository {
    private static final Logger logger = Logger.getLogger(GoonjBaseRepository.class);
    private final IntegratingEntityStatusRepository integratingEntityStatusRepository;
    private final RestTemplate goonjRestTemplate;
    private final GoonjConfig goonjConfig;
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

    protected <T> T getResponse(Date dateTime, String resource,  Class<T> returnType) {
        URI uri = URI.create(String.format("%s/%s?dateTimestamp=%s", goonjConfig.getAppUrl(), resource,
                DateTimeUtil.formatDateTime(dateTime)));
        ResponseEntity<T> responseEntity = goonjRestTemplate.exchange(uri, HttpMethod.GET, null, returnType);
        if(responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        }
        logger.error(String.format("Failed to fetch data for resource %s, response status code is %s", resource, responseEntity.getStatusCode()));
        throw new HttpServerErrorException(responseEntity.getStatusCode());
    }

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
        URI uri = URI.create(String.format("%s/%s", goonjConfig.getAppUrl(), resource));
        ParameterizedTypeReference<HashMap<String, Object>[]> responseType = new ParameterizedTypeReference<>() {};
        try {
            ResponseEntity<HashMap<String, Object>[]> responseEntity = goonjRestTemplate.exchange(uri, HttpMethod.POST, requestEntity, responseType);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity.getBody();
            }
            logger.error(String.format("Failed to create resource %s,  response status code is %s", resource, responseEntity.getStatusCode()));
            throw handleError(responseEntity, responseEntity.getStatusCode());
        } catch(Exception e) {
            logger.error("Error request body:" + ObjectJsonMapper.writeValueAsString(requestEntity.getBody()), e);
            throw e;
        }
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

    public abstract HashMap<String, Object>[] fetchEvents();

    public abstract List<String> fetchDeletionEvents();

    public abstract HashMap<String, Object>[] createEvent(Subject subject, GeneralEncounter encounter);
    public boolean wasEventCreatedSuccessfully(HashMap<String, Object>[] response) {
        return (response != null && response[0].get("errorCode") == null);
    }

}
