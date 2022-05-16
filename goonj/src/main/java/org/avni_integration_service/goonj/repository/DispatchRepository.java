package org.avni_integration_service.goonj.repository;

import org.avni_integration_service.goonj.config.GoonjConfig;
import org.avni_integration_service.goonj.domain.AuthResponse;
import org.avni_integration_service.goonj.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;

@Component
public class DispatchRepository extends BaseRepository {
    private final RestTemplate restTemplate;
    private final GoonjConfig goonjConfig;

    @Autowired
    public DispatchRepository(RestTemplate restTemplate, GoonjConfig goonjConfig) {
        this.restTemplate = restTemplate;
        this.goonjConfig = goonjConfig;
    }

    public HashMap<String, Object>[] getDispatches(AuthResponse authResponse, LocalDateTime dateTime) {
        URI uri = URI.create(String.format("http://%s/services/apexrest/v1/DispatchService/getDispatches?dateTimestamp=%s", goonjConfig.getAppUrl(), DateTimeUtil.formatDateTime(dateTime)));
        ParameterizedTypeReference<HashMap<String, Object>[]> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<HashMap<String, Object>[]> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, getHeaders(authResponse), responseType);
        return responseEntity.getBody();
    }
}
