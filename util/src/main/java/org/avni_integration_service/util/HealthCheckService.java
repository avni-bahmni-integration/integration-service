package org.avni_integration_service.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Component
public class HealthCheckService {
    private static final String PING_BASE_URL = "https://hc-ping.com/";

    private final RestTemplate restTemplate;

    @Autowired
    public HealthCheckService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void verify(String uuid) {
        restTemplate.exchange(URI.create(String.format("%s%s", PING_BASE_URL, uuid)), HttpMethod.GET, null, String.class);
    }
}
