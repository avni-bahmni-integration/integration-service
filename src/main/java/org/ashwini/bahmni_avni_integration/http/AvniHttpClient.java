package org.ashwini.bahmni_avni_integration.http;

import org.ashwini.bahmni_avni_integration.auth.AuthenticationHelper;
import org.ashwini.bahmni_avni_integration.web.response.CognitoDetailsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AvniHttpClient {

    @Value("${avni.api.url}")
    private String AVNI_API_URL;


    public ResponseEntity<String> get(String url) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<CognitoDetailsResponse> response = restTemplate.getForEntity(apiUrl("/cognito-details"), CognitoDetailsResponse.class);
        CognitoDetailsResponse cognitoDetails = response.getBody();
        AuthenticationHelper helper = new AuthenticationHelper(cognitoDetails.getPoolId(), cognitoDetails.getClientId(), "");
        String authToken = helper.PerformSRPAuthentication("hirent@jsscp", "password");

        HttpHeaders headers = new HttpHeaders();
        headers.add("auth-token", authToken);

        return restTemplate.exchange(apiUrl(url), HttpMethod.GET, new HttpEntity<String>(headers), String.class);

    }

    private String apiUrl(String url) {
        return String.format(AVNI_API_URL + url);
    }
}