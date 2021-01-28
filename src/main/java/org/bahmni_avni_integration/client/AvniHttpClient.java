package org.bahmni_avni_integration.client;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.auth.AuthenticationHelper;
import org.bahmni_avni_integration.web.response.CognitoDetailsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
public class AvniHttpClient {
    @Value("${avni.api.url}")
    private String AVNI_API_URL;

    @Value("${avni.impl.username}")
    private String AVNI_IMPL_USER;

    @Value("${avni.impl.password}")
    private String AVNI_IMPL_PASSWORD;

    private String authToken;

    private static Logger logger = Logger.getLogger(AvniHttpClient.class);

    public <T> ResponseEntity<T> get(String url, Map<String, String> queryParams, Class<T> returnType) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("auth-token", fetchAuthToken());

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl(url));
        for (var entry : queryParams.entrySet()) {
            builder.queryParam(entry.getKey(), entry.getValue());
        }

        String uriString = builder.toUriString();
        logger.info("GETting from: " + uriString);
        return restTemplate.exchange(uriString, HttpMethod.GET, new HttpEntity<String>(headers), returnType);
    }

    private String fetchAuthToken() {
        if (authToken != null && !authToken.isEmpty()) {
            return authToken;
        }
        RestTemplate restTemplate = new RestTemplate();
        logger.info("Getting cognito details");
        ResponseEntity<CognitoDetailsResponse> response = restTemplate.getForEntity(apiUrl("/cognito-details"), CognitoDetailsResponse.class);
        CognitoDetailsResponse cognitoDetails = response.getBody();
        AuthenticationHelper helper = new AuthenticationHelper(cognitoDetails.getPoolId(), cognitoDetails.getClientId(), "");
        authToken = helper.PerformSRPAuthentication(AVNI_IMPL_USER, AVNI_IMPL_PASSWORD);
        return authToken;
    }

    private String apiUrl(String url) {
        return String.format(AVNI_API_URL + url);
    }
}