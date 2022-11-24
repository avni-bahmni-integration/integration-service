package org.avni_integration_service.amrit.service;

import org.apache.log4j.Logger;
import org.avni_integration_service.amrit.config.AmritApplicationConfig;
import org.avni_integration_service.amrit.dto.LoginRequest;
import org.avni_integration_service.amrit.util.DateTimeUtil;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AmritTokenService {

    private static final Logger logger = Logger.getLogger(AmritTokenService.class);
    public static final int UNITS = 10;
    private final AmritApplicationConfig amritConfig;
    private final RestTemplate restTemplate;
    private String tokenCache;
    private Date tokenGenerationTime;

    public AmritTokenService(AmritApplicationConfig amritConfig) {
        this.amritConfig = amritConfig;
        this.restTemplate = new RestTemplate();
    }

    public String getRefreshedToken() {
        if (tokenCache == null || hasTokenExpired()) {
            loginAndGenerateToken();
        } else {
            System.out.println("Token still valid");
            logger.info("Token still valid");
        }
        return tokenCache;
    }

    public void loginAndGenerateToken() {
        logger.info("Token expired, fetching new token");
        tokenCache = loginWithCredentials();
        tokenGenerationTime = new Date();
    }

    private boolean hasTokenExpired() {
        return new Date().after(DateTimeUtil.addTimeToJavaUtilDate(tokenGenerationTime, UNITS, Calendar.MINUTE));
    }

    public String loginWithCredentials() {
        URI uri = URI.create(String.format("%s/%s/user/userAuthenticate/", amritConfig.getAmritServerUrl(), amritConfig.getCommonApiPrefix()));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ParameterizedTypeReference<HashMap<String, Object>> responseType = new ParameterizedTypeReference<>() {};
        LoginRequest loginContract = new LoginRequest(amritConfig.getAmritApiUser(), amritConfig.getAmritApiPassword());
        ResponseEntity<HashMap<String, Object>> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>(loginContract, headers), responseType);
        HashMap<String, Object> body = responseEntity.getBody();
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            int statusCode = (int) body.get("statusCode");
            if(statusCode != 200) {
                throw new RuntimeException(String.format("Error fetching token, Status code: %d, Error Status: %s, Error message: %s", statusCode,
                        (String) body.get("status"),(String) body.get("errorMessage")));
            }
            Map map = (Map) body.get("data");
            return (String) map.get("key");
        }
        throw new RuntimeException("Error during login");
    }
}