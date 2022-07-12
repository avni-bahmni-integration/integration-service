package org.avni_integration_service.amrit.repository;

import org.avni_integration_service.amrit.config.AmritApplicationConfig;
import org.avni_integration_service.amrit.contract.LoginContract;
import org.hibernate.annotations.common.util.impl.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Repository
public class AmritUserRepository {
    private final AmritApplicationConfig amritConfig;
    private final RestTemplate restTemplate;

    @Autowired
    public AmritUserRepository(AmritApplicationConfig amritConfig, @Qualifier("AmritRestTemplate") RestTemplate restTemplate) {
        this.amritConfig = amritConfig;
        this.restTemplate = restTemplate;
    }

    public String login() {
        URI uri = URI.create(String.format("%s/commonapi-v1.0/user/userAuthenticate/", amritConfig.getAmritServerUrl()));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ParameterizedTypeReference<HashMap<String, Object>> responseType = new ParameterizedTypeReference<>() {};
        LoginContract loginContract = new LoginContract(amritConfig.getAmritApiUser(), amritConfig.getAmritApiPassword());
        ResponseEntity<HashMap<String, Object>> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>(loginContract, headers), responseType);
        HashMap<String, Object> body = responseEntity.getBody();
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            Map map = (Map) body.get("data");
            return (String) map.get("key");
        }
        throw new RuntimeException("Error during login");
    }
}
