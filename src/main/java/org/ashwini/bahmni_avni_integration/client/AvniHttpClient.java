package org.ashwini.bahmni_avni_integration.client;

import org.ashwini.bahmni_avni_integration.auth.AuthenticationHelper;
import org.ashwini.bahmni_avni_integration.web.response.CognitoDetailsResponse;
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


    public ResponseEntity<String> get(String url, Map<String, String> queryParams) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("auth-token", fetchAuthToken());

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl(url));
        for (var entry : queryParams.entrySet()) {
            builder.queryParam(entry.getKey(), entry.getValue());
        }

        return restTemplate.exchange(builder.toUriString(), HttpMethod.GET, new HttpEntity<String>(headers), String.class);

    }

    private String fetchAuthToken() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<CognitoDetailsResponse> response = restTemplate.getForEntity(apiUrl("/cognito-details"), CognitoDetailsResponse.class);
        CognitoDetailsResponse cognitoDetails = response.getBody();
        AuthenticationHelper helper = new AuthenticationHelper(cognitoDetails.getPoolId(), cognitoDetails.getClientId(), "");
        return helper.PerformSRPAuthentication(AVNI_IMPL_USER, AVNI_IMPL_PASSWORD);
    }

    private String apiUrl(String url) {
        return String.format(AVNI_API_URL + url);
    }
}