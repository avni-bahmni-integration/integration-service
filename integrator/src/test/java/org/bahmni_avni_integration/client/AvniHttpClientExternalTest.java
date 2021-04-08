package org.bahmni_avni_integration.client;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AvniHttpClientExternalTest {
    @Autowired
    private AvniHttpClient avniHttpClient;

    @Test
    @Disabled
    void refreshAuthToken() {
        avniHttpClient.fetchAuthToken();
        avniHttpClient.get("/concept", HashMap.class);
        avniHttpClient.refreshToken();
        avniHttpClient.get("/concept", HashMap.class);
    }

    @Test
    void clearAuthInformation() {
        avniHttpClient.fetchAuthToken();
        avniHttpClient.get("/concept", HashMap.class);
        avniHttpClient.clearAuthInformation();
        avniHttpClient.get("/concept", HashMap.class);
    }
}