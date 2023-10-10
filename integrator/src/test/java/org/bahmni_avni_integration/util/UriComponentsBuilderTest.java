package org.bahmni_avni_integration.util;

import org.junit.jupiter.api.Test;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UriComponentsBuilderTest {
    @Test
    public void encoding() {
        String foo = "https://app.avniproject.org/api/encounters?concepts={\"Bahmni Entity UUID [Bahmni]\":\"bdccc91b-5084-41c5-bba6-6d6e3b886bea\"}";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(foo);
        assertEquals("https://app.avniproject.org/api/encounters?concepts=%7B%22Bahmni%20Entity%20UUID%20%5BBahmni%5D%22:%22bdccc91b-5084-41c5-bba6-6d6e3b886bea%22%7D", builder.build().encode().toUri().toString());
    }
}
