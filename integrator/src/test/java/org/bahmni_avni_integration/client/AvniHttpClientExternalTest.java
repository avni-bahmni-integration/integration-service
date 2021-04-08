package org.bahmni_avni_integration.client;

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
    void refreshAuthToken() throws InterruptedException {
        avniHttpClient.fetchAuthToken();
        avniHttpClient.get("/concept", HashMap.class);
        Thread.sleep(5000);
        avniHttpClient.refreshToken();
        avniHttpClient.get("/concept", HashMap.class);
    }
}