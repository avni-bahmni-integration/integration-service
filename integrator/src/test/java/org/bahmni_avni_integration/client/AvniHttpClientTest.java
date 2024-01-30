package org.bahmni_avni_integration.client;

import org.bahmni_avni_integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class AvniHttpClientTest extends AbstractIntegrationTest {
    @Autowired
    private AvniHttpClient avniHttpClient;

    @Test
    void makeGetUri() {
        HashMap<String, String> queryParams = new HashMap<>() {{
            put("foo", "bar");
        }};
        assertEquals("https://staging.avniproject.org/baz?version=2&foo=bar", avniHttpClient.makeGetUri("/baz", queryParams).toString());
        assertEquals("https://staging.avniproject.org?version=2&foo=bar", avniHttpClient.makeGetUri("", queryParams).toString());
        assertEquals("https://staging.avniproject.org/baz?version=2", avniHttpClient.makeGetUri("/baz", new HashMap<>()).toString());
    }
}
