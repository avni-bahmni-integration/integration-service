package org.avni_integration_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Component
public class PowerConfig {
    @Value("${power.exotel.apiKey}")
    private String exotelAPIKey;

    @Value("${power.exotel.apiToken}")
    private String exotelAPIToken;

    @Value("${power.exotel.accountSID}")
    private String exotelAccountSID;

    @Value("${power.exotel.subdomain}")
    private String exotelSubdomain;

    public String getExotelAPIKey() {
        return exotelAPIKey;
    }

    public String getExotelAPIToken() {
        return exotelAPIToken;
    }

    public String getExotelAccountSID() {
        return exotelAccountSID;
    }

    public String getExotelSubdomain() {
        return exotelSubdomain;
    }

    public String getCallDetailsAPI(String sid) {
        String baseURI = String.format("https://%s/v1/Accounts/%s/Calls", this.exotelSubdomain, this.exotelAccountSID);
        return sid == null ? baseURI : baseURI + String.format("/%s", sid);
    }

    @Bean("PowerRestTemplate")
    RestTemplate restTemplate() {
        return new RestTemplateBuilder()
                .interceptors((httpRequest, bytes, execution) -> {
                    String authorizationEncoding = Base64.getEncoder().encodeToString(
                            (String.format("%s:%s", this.exotelAPIKey, this.exotelAPIToken)).getBytes()
                    );
                    httpRequest.getHeaders().add(HttpHeaders.AUTHORIZATION, "Basic " + authorizationEncoding);
                    httpRequest.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
                    return execution.execute(httpRequest, bytes);
                })
                .build();
    }
}
