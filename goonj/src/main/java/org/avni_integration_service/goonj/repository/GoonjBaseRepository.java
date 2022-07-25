package org.avni_integration_service.goonj.repository;

import org.apache.log4j.Logger;
import org.avni_integration_service.avni.domain.GeneralEncounter;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.goonj.config.GoonjConfig;
import org.avni_integration_service.goonj.util.DateTimeUtil;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public GoonjBaseRepository(IntegratingEntityStatusRepository integratingEntityStatusRepository,
                               RestTemplate restTemplate, GoonjConfig goonjConfig, String entityType) {
        this.integratingEntityStatusRepository = integratingEntityStatusRepository;
        this.goonjRestTemplate = restTemplate;
        this.goonjConfig = goonjConfig;
        this.entityType = entityType;
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

    protected HashMap<String, Object> getSingleEntityResponse(String resource, String uuid) {
        URI uri = URI.create(String.format("%s/%s?uuid=%s", goonjConfig.getAppUrl(), resource, uuid));
        ParameterizedTypeReference<HashMap<String, Object>> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<HashMap<String, Object>> responseEntity = goonjRestTemplate.exchange(uri, HttpMethod.GET, null, responseType);
        return responseEntity.getBody();
    }

    protected HashMap<String, Object>[] createSingleEntity(String resource, HttpEntity<?> requestEntity) throws RestClientResponseException {
        URI uri = URI.create(String.format("%s/%s", goonjConfig.getAppUrl(), resource));
        ParameterizedTypeReference<HashMap<String, Object>[]> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<HashMap<String, Object>[]> responseEntity = goonjRestTemplate.exchange(uri, HttpMethod.POST, requestEntity, responseType);
        if(responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        }
        logger.error(String.format("Failed to create resource %s,  response status code is %s", resource, responseEntity.getStatusCode()));
        throw handleError(responseEntity, responseEntity.getStatusCode());
    }

    protected RestClientException handleError(ResponseEntity<HashMap<String, Object>[]> responseEntity, HttpStatus statusCode) {
        HashMap<String, Object>[] responseBody = responseEntity.getBody();
        String message = (String) responseBody[0].get("message");

        switch (statusCode.series()) {
            case CLIENT_ERROR:
                return HttpClientErrorException.create(message, statusCode, null, responseEntity.getHeaders(), null, null);
            case SERVER_ERROR:
                return HttpServerErrorException.create(message, statusCode, null, responseEntity.getHeaders(), null, null);
            default:
                return new UnknownHttpStatusCodeException(message, statusCode.value(), null, responseEntity.getHeaders(), null, null);
        }
    }

    public abstract HashMap<String, Object>[] fetchEvents();

    public abstract List<String> fetchDeletionEvents();

    public abstract HashMap<String, Object>[] createEvent(Subject subject, GeneralEncounter encounter);
    public boolean wasEventCreatedSuccessfully(HashMap<String, Object>[] response) {
        return (response != null && response[0].get("errorCode") == null);
    }

}
