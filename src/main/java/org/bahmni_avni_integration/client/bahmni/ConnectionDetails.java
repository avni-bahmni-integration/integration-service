package org.bahmni_avni_integration.client.bahmni;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class ConnectionDetails {
    private String authUrl;
    private String userId;
    private String password;
    private int connectionTimeout;
    private int readTimeout;
    private PoolingHttpClientConnectionManager connectionManager;

    public ConnectionDetails(String authUrl, String userId, String password, int connectionTimeout, int readTimeout) {
        this.authUrl = authUrl;
        this.userId = userId;
        this.password = password;
        this.connectionTimeout = connectionTimeout;
        this.readTimeout = readTimeout;
    }

    public ConnectionDetails(String authUrl, String userId, String password, int connectionTimeout, int readTimeout, PoolingHttpClientConnectionManager connectionManager) {
        this.authUrl = authUrl;
        this.userId = userId;
        this.password = password;
        this.connectionTimeout = connectionTimeout;
        this.readTimeout = readTimeout;
        this.connectionManager = connectionManager;
    }

    public String getUserId() {
        return this.userId;
    }

    public String getPassword() {
        return this.password;
    }

    public int getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public int getReadTimeout() {
        return this.readTimeout;
    }

    public String getAuthUrl() {
        return this.authUrl;
    }

    public PoolingHttpClientConnectionManager getConnectionManager() {
        return this.connectionManager;
    }
}