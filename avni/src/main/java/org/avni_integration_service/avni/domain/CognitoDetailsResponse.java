package org.avni_integration_service.avni.domain;

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
