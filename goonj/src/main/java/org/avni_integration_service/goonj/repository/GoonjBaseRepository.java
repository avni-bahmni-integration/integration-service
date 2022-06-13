package org.avni_integration_service.goonj.repository;

import org.apache.log4j.Logger;
import org.avni_integration_service.goonj.config.GoonjConfig;
import org.avni_integration_service.goonj.util.DateTimeUtil;
import org.avni_integration_service.goonj.worker.AvniGoonjErrorRecordsWorker;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.avni_integration_service.util.FormatAndParseUtil;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;

public abstract class GoonjBaseRepository {
    private final IntegratingEntityStatusRepository integratingEntityStatusRepository;
    private final RestTemplate goonjRestTemplate;
    private final GoonjConfig goonjConfig;
    private static final Logger logger = Logger.getLogger(GoonjBaseRepository.class);

    private final String entityType;

    public GoonjBaseRepository(IntegratingEntityStatusRepository integratingEntityStatusRepository,
                               RestTemplate restTemplate, GoonjConfig goonjConfig, String entityType) {
        this.integratingEntityStatusRepository = integratingEntityStatusRepository;
        this.goonjRestTemplate = restTemplate;
        this.goonjConfig = goonjConfig;
        this.entityType = entityType;
    }

    protected HashMap<String, Object>[] getResponse(LocalDateTime dateTime, String resource) {
        URI uri = URI.create(String.format("%s/%s?dateTimestamp=%s", goonjConfig.getAppUrl(), resource, DateTimeUtil.formatDateTime(dateTime)));
        System.out.println(String.format("Calling: %s", uri.toString()));
        ParameterizedTypeReference<HashMap<String, Object>[]> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<HashMap<String, Object>[]> responseEntity = goonjRestTemplate.exchange(uri, HttpMethod.GET, null, responseType);
        return responseEntity.getBody();
    }

    protected Date getCutOffDate() {
        return integratingEntityStatusRepository.findByEntityType(entityType).getReadUptoDateTime();
    }

    protected LocalDateTime getCutOffDateTime() {
        return FormatAndParseUtil.toLocalDateTime(getCutOffDate());
    }
    public abstract HashMap<String, Object>[] fetchEvents();

    protected HashMap<String, Object> getSingleEntityResponse(String resource, String uuid) {
        URI uri = URI.create(String.format("%s/services/apexrest/v1/%s/%s?dateTimestamp=%s", goonjConfig.getAppUrl(), resource, uuid));
        ParameterizedTypeReference<HashMap<String, Object>> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<HashMap<String, Object>> responseEntity = goonjRestTemplate.exchange(uri, HttpMethod.GET, null, responseType);
        return responseEntity.getBody();
    }
}
