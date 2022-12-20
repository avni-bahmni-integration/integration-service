package org.avni_integration_service.avni.client;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.BeforeTestClass;

import java.util.HashMap;

@SpringBootTest(classes = AvniHttpClient.class)
@Disabled
class AvniHttpClientExternalTest {
    @Autowired
    private AvniHttpClient avniHttpClient;
    private AvniSession avniConnectionDetails;

    @BeforeTestClass
    public void setup() {
        avniConnectionDetails = new AvniSession(null, null, null, true);
        avniHttpClient.setAvniSession(avniConnectionDetails);
    }

    @Test
    @Disabled
    void refreshAuthToken() {
        avniHttpClient.fetchAuthToken();
        avniHttpClient.get("/concept", HashMap.class);
        avniConnectionDetails.refreshToken();
        avniHttpClient.get("/concept", HashMap.class);
    }

    @Test
    void clearAuthInformation() {
        avniHttpClient.fetchAuthToken();
        avniHttpClient.get("/concept", HashMap.class);
        avniConnectionDetails.clearAuthInformation();
        avniHttpClient.get("/concept", HashMap.class);
    }
}
