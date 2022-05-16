package org.avni_integration_service.goonj.repository;

import org.avni_integration_service.goonj.config.GoonjConfig;
import org.avni_integration_service.goonj.domain.AuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Component
public class SalesForceUserRepository {
    private final RestTemplate restTemplate;
    private final GoonjConfig goonjConfig;

    @Autowired
    public SalesForceUserRepository(RestTemplate restTemplate, GoonjConfig goonjConfig) {
        this.restTemplate = restTemplate;
        this.goonjConfig = goonjConfig;
    }

    public AuthResponse login() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("username", goonjConfig.getLoginUserName());
        map.add("password", goonjConfig.getLoginPassword());
        map.add("grant_type", "password");
        map.add("client_id", goonjConfig.getClientId());
        map.add("client_secret", goonjConfig.getClientSecret());
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(URI.create(goonjConfig.getSalesForceAuthUrl()), request, AuthResponse.class);
        return response.getBody();
    }
}
