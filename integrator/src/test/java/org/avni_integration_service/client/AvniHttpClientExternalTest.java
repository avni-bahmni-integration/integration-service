package org.avni_integration_service.client;

import org.avni_integration_service.avni.client.AvniHttpClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

@SpringBootTest
@Disabled
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
