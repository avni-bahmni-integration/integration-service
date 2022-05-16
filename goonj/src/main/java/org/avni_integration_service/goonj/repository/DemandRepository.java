package org.avni_integration_service.goonj.repository;

import org.avni_integration_service.goonj.config.GoonjConfig;
import org.avni_integration_service.goonj.domain.AuthResponse;
import org.avni_integration_service.goonj.domain.Demand;
import org.avni_integration_service.goonj.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDateTime;

@Component
public class DemandRepository extends BaseRepository {
    private final RestTemplate restTemplate;
    private final GoonjConfig goonjConfig;

    @Autowired
    public DemandRepository(RestTemplate restTemplate, GoonjConfig goonjConfig) {
        this.restTemplate = restTemplate;
        this.goonjConfig = goonjConfig;
    }

    public Demand[] getDemands(AuthResponse authResponse, LocalDateTime dateTime) {
        URI uri = URI.create(String.format("http://%s/services/apexrest/v1/DemandService/getDemands?dateTimestamp=%s", goonjConfig.getAppUrl(), DateTimeUtil.formatDateTime(dateTime)));
        ResponseEntity<Demand[]> response = restTemplate.exchange(uri, HttpMethod.GET, getHeaders(authResponse), Demand[].class);
        return response.getBody();
    }
}
