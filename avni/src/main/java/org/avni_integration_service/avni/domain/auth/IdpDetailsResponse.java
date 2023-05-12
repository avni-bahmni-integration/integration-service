package org.avni_integration_service.avni.domain.auth;

public class IdpDetailsResponse {
    private KeycloakDetails keycloak;
    private CognitoDetails cognito;

    public KeycloakDetails getKeycloak() {
        return keycloak;
    }

    public void setKeycloak(KeycloakDetails keycloak) {
        this.keycloak = keycloak;
    }

    public CognitoDetails getCognito() {
        return cognito;
    }

    public void setCognito(CognitoDetails cognito) {
        this.cognito = cognito;
    }

}
