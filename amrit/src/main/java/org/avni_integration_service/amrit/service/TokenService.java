package org.avni_integration_service.amrit.service;

import org.apache.log4j.Logger;
import org.avni_integration_service.amrit.config.AmritApplicationConfig;
import org.avni_integration_service.amrit.contract.LoginContract;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.Clock;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class TokenService {

    private static final Logger logger = Logger.getLogger(TokenService.class);
    private final Duration clockSkew = Duration.ofSeconds(60);
    private final Clock clock = Clock.systemUTC();
    private final AmritApplicationConfig amritConfig;
    private final RestTemplate restTemplate;
    private String tokenCache;

    public TokenService(AmritApplicationConfig amritConfig, RestTemplate restTemplate) {
        this.amritConfig = amritConfig;
        this.restTemplate = restTemplate;
    }

    public String getRefreshedToken() {
        if (tokenCache == null) {
            logger.info("Token expired, fetching new token");
            tokenCache = loginWithCredentials();
        } else {
            logger.debug("Token still valid");
        }

        return tokenCache;
    }
    //todo implement expiration and refresh logic for token

    public String loginWithCredentials() {
        URI uri = URI.create(String.format("%s/%s/user/userAuthenticate/", amritConfig.getAmritServerUrl(), amritConfig.getCommonApiPrefix()));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ParameterizedTypeReference<HashMap<String, Object>> responseType = new ParameterizedTypeReference<>() {
        };
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