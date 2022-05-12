package org.avni_integration_service.bahmni.client.openmrs;

public class OpenMRSAuthenticationResponse {
    private String sessionId;
    private boolean authenticated;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }
}
