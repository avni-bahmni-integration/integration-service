package org.avni_integration_service.avni.client;

import org.avni_integration_service.avni.domain.auth.IdpDetailsResponse;
import org.avni_integration_service.avni.domain.auth.KeycloakDetails;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
class AvniSessionExternalTest {
    @Test
    void getIdToken() {
        AvniSession avniSession = new AvniSession("http://localhost:8021", "vin@jsscp", "password", true, IdpType.Keycloak);
        IdpDetailsResponse idpDetailsResponse = new IdpDetailsResponse();
        KeycloakDetails keycloak = new KeycloakDetails();
        keycloak.setAuthServerUrl("http://localhost:8080");
        keycloak.setClientId("avni-client");
        keycloak.setGrantType("password");
        keycloak.setRealm("On-premise");
        keycloak.setScope("openid");
        idpDetailsResponse.setKeycloak(keycloak);
        String idToken = avniSession.fetchIdToken(idpDetailsResponse);
    }
}
