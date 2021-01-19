package org.ashwini.bahmni_avni_integration.web.response;

public class CognitoDetailsResponse {
    private String poolId;
    private String clientId;

    public String getPoolId() {
        return poolId;
    }

    public void setPoolId(String poolId) {
        this.poolId = poolId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}