package org.avni_integration_service.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import org.apache.log4j.Logger;

@Component
public class HealthCheckService {
    private static final String PING_BASE_URL = "https://hc-ping.com";

    private static final Logger logger = Logger.getLogger(HealthCheckService.class);

    private final RestTemplate restTemplate;

    @Value("${healthcheck.ping.key}")
    private String healthCheckPingKey;

    @Autowired
    public HealthCheckService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private void ping(String slug, String status) {
        try {
            if (!healthCheckPingKey.equals("dummy"))
                restTemplate.exchange(URI.create(String.format("%s/%s/%s/%s", PING_BASE_URL, healthCheckPingKey, slug, status)), HttpMethod.GET, null, String.class);
        }
        catch(Exception e) {
            logger.error("Health check ping failed:", e);
        }
    }

    public void start(String slug) {
        ping(slug, "start");
    }
    public void success(String slug) {
        ping(slug, "0");
    }
    public void failure(String slug) {
        ping(slug, "fail");
    }
}
