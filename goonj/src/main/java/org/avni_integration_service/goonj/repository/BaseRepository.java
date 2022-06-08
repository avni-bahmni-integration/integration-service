package org.avni_integration_service.goonj.repository;

import org.avni_integration_service.goonj.config.GoonjConfig;
import org.avni_integration_service.goonj.domain.AuthResponse;
import org.avni_integration_service.goonj.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;

public class BaseRepository {
    private final RestTemplate goonjRestTemplate;
    private final GoonjConfig goonjConfig;

    @Autowired
    public BaseRepository(@Qualifier("GoonjRestTemplate") RestTemplate restTemplate, GoonjConfig goonjConfig) {
        this.goonjRestTemplate = restTemplate;
        this.goonjConfig = goonjConfig;
    }


    protected HashMap<String, Object>[] getResponse(LocalDateTime dateTime, String resource) {
        URI uri = URI.create(String.format("%s/services/apexrest/v1/%s?dateTimestamp=%s", goonjConfig.getAppUrl(), resource, DateTimeUtil.formatDateTime(dateTime)));
        ParameterizedTypeReference<HashMap<String, Object>[]> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<HashMap<String, Object>[]> responseEntity = goonjRestTemplate.exchange(uri, HttpMethod.GET, null, responseType);
        return responseEntity.getBody();
    }
}
