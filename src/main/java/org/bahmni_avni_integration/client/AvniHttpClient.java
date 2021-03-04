package org.bahmni_avni_integration.client;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.auth.AuthenticationHelper;
import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.util.ObjectJsonMapper;
import org.bahmni_avni_integration.web.response.CognitoDetailsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Component
public class AvniHttpClient {
    @Value("${avni.api.url}")
    private String AVNI_API_URL;

    @Value("${avni.impl.username}")
    private String AVNI_IMPL_USER;

    @Value("${avni.impl.password}")
    private String AVNI_IMPL_PASSWORD;

    @Autowired
    RestTemplate restTemplate;

    private String authToken;

    private static Logger logger = Logger.getLogger(AvniHttpClient.class);

    public <T> ResponseEntity<T> get(String url, Map<String, String> queryParams, Class<T> returnType) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl(url));
        for (var entry : queryParams.entrySet()) {
            builder.queryParam(entry.getKey(), entry.getValue());
        }

        URI uri = builder.build().toUri();
        return restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<String>(authHeaders()), returnType);
    }

    public <T> ResponseEntity<T> get(String url, Class<T> returnType) {
        return get(url, new HashMap<>(), returnType);
    }

    public <T, U> ResponseEntity<U> post(String url, T t, Class<U> returnType) {
        logger.info(String.format("POST: %s", url));
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl(url));
        String json = ObjectJsonMapper.writeValueAsString(t);
        return restTemplate.exchange(builder.build().toUri(), HttpMethod.POST, new HttpEntity<>(json, authHeaders()), returnType);
    }

    public <T, U> ResponseEntity<U> put(String url, T requestBody, Class<U> returnType) {
        logger.info(String.format("PUT: %s", url));
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl(url));
        return restTemplate.exchange(builder.build().toUri(), HttpMethod.PUT, new HttpEntity<>(requestBody, authHeaders()), returnType);
    }

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("auth-token", fetchAuthToken());
        return headers;
    }

    private String fetchAuthToken() {
        if (authToken != null && !authToken.isEmpty()) {
            return authToken;
        }

        RestTemplate restTemplate = new RestTemplate();
        logger.debug("Getting cognito details");
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