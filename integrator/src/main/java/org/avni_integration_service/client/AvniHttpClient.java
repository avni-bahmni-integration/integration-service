package org.avni_integration_service.client;

import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import org.apache.log4j.Logger;
import org.avni_integration_service.auth.AuthenticationHelper;
import org.avni_integration_service.util.ObjectJsonMapper;
import org.avni_integration_service.web.response.CognitoDetailsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
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

    @Value("${authenticate.with.avni}")
    private boolean authenticateWithAvni;

    @Autowired
    private RestTemplate restTemplate;

    private static final Logger logger = Logger.getLogger(AvniHttpClient.class);
    private AuthenticationResultType authenticationResultType;
    private AuthenticationHelper helper;

    public <T> ResponseEntity<T> get(String url, Map<String, String> queryParams, Class<T> returnType) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl(url));
        for (var entry : queryParams.entrySet()) {
            builder.queryParam(entry.getKey(), entry.getValue());
        }

        URI uri = builder.build().toUri();
        return getResponseEntity(returnType, uri, HttpMethod.GET, null);
    }

    private <T> ResponseEntity<T> getResponseEntity(Class<T> returnType, URI uri, HttpMethod method, String json) {
        try {
            logger.debug("%s %s".formatted(method.name(), uri.toString()));
            return restTemplate.exchange(uri, method, getRequestEntity(json), returnType);
        } catch (HttpServerErrorException.InternalServerError e) {
            if (e.getMessage().contains("TokenExpiredException")) {
                this.clearAuthInformation();
                return restTemplate.exchange(uri, method, getRequestEntity(json), returnType);
            }
            throw e;
        }
    }

    private HttpEntity<String> getRequestEntity(String json) {
        return json == null ? new HttpEntity<>(authHeaders()) : new HttpEntity<>(json, authHeaders());
    }

    public <T> ResponseEntity<T> get(String url, Class<T> returnType) {
        return get(url, new HashMap<>(), returnType);
    }

    public <T, U> ResponseEntity<U> post(String url, T t, Class<U> returnType) {
        logger.info(String.format("POST: %s", url));
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl(url));
        String json = ObjectJsonMapper.writeValueAsString(t);
        return getResponseEntity(returnType, builder.build().toUri(), HttpMethod.POST, json);
    }

    public <T, U> ResponseEntity<U> put(String url, T requestBody, Class<U> returnType) {
        logger.info(String.format("PUT: %s", url));
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl(url));
        try {
            return restTemplate.exchange(builder.build().toUri(), HttpMethod.PUT, new HttpEntity<>(requestBody, authHeaders()), returnType);
        } catch (HttpServerErrorException.InternalServerError e) {
            if (e.getMessage().contains("TokenExpiredException")) {
                clearAuthInformation();
                return restTemplate.exchange(builder.build().toUri(), HttpMethod.PUT, new HttpEntity<>(requestBody, authHeaders()), returnType);
            }
            throw e;
        }
    }

    public void refreshToken() {
        authenticationResultType = helper.refresh(authenticationResultType.getRefreshToken(), authenticationResultType.getIdToken());
    }

    //        couldn't get refresh token to work hence clearing auth information when token expires so that a new token is taken
    public void clearAuthInformation() {
        authenticationResultType = null;
    }

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        if (authenticateWithAvni)
            headers.add("auth-token", fetchAuthToken());
        headers.add("content-type", "application/json");
        return headers;
    }

    public String fetchAuthToken() {
        if (authenticationResultType != null && !authenticationResultType.getIdToken().isEmpty()) {
            return authenticationResultType.getIdToken();
        }

        RestTemplate restTemplate = new RestTemplate();
        logger.debug("Getting cognito details");
        ResponseEntity<CognitoDetailsResponse> response = restTemplate.getForEntity(apiUrl("/cognito-details"), CognitoDetailsResponse.class);
        CognitoDetailsResponse cognitoDetails = response.getBody();
        helper = new AuthenticationHelper(cognitoDetails.getPoolId(), cognitoDetails.getClientId());
        authenticationResultType = helper.performSRPAuthentication(AVNI_IMPL_USER, AVNI_IMPL_PASSWORD);
        return authenticationResultType.getIdToken();
    }

    private String apiUrl(String url) {
        return String.format("%s%s", AVNI_API_URL, url);
    }
}
