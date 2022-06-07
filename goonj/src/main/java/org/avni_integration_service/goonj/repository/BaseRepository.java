package org.avni_integration_service.goonj.repository;

import org.avni_integration_service.goonj.config.GoonjConfig;
import org.avni_integration_service.goonj.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;

public class BaseRepository {
    private final RestTemplate restTemplate;
    private final GoonjConfig goonjConfig;

    @Autowired
    public BaseRepository(@Value("goonjRestTemplate") RestTemplate restTemplate, GoonjConfig goonjConfig) {
        this.restTemplate = restTemplate;
        this.goonjConfig = goonjConfig;
    }

    protected HashMap<String, Object>[] getResponse(LocalDateTime dateTime, String resource) {
        URI uri = URI.create(String.format("%s/services/apexrest/v1/%s?dateTimestamp=%s", goonjConfig.getAppUrl(), resource, DateTimeUtil.formatDateTime(dateTime)));
        ParameterizedTypeReference<HashMap<String, Object>[]> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<HashMap<String, Object>[]> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, null, responseType);
        return responseEntity.getBody();
    }
}
