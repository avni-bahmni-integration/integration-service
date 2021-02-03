package org.bahmni_avni_integration.client.bahmni;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.IOException;

public class HttpClientInternal {
    private final int connectTimeout;
    private final int readTimeout;
    private CloseableHttpClient closeableHttpClient;
    private PoolingHttpClientConnectionManager connectionManager;

    HttpClientInternal(int connectionTimeout, int readTimeout) {
        this.connectTimeout = connectionTimeout;
        this.readTimeout = readTimeout;
    }

    HttpClientInternal(int connectionTimeout, int readTimeout, PoolingHttpClientConnectionManager connectionManager) {
        this.connectTimeout = connectionTimeout;
        this.readTimeout = readTimeout;
        this.connectionManager = connectionManager;
    }


    public HttpResponse get(HttpRequestDetails requestDetails) {
        return get(requestDetails, new HttpHeaders());
    }

    public HttpResponse get(HttpRequestDetails requestDetails, HttpHeaders httpHeaders) {
        HttpGet httpGet = new HttpGet(requestDetails.getUri());
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(readTimeout)
                .setSocketTimeout(connectTimeout)
                .build();
        if (connectionManager == null) {
            connectionManager = new PoolingHttpClientConnectionManager();
        }
        closeableHttpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connectionManager)
                .build();
        requestDetails.addDetailsTo(httpGet);
        httpHeaders.addTo(httpGet);

        try {
            return closeableHttpClient.execute(httpGet);
        } catch (IOException e) {
            throw new WebClientsException("Error executing request", e);
        }
    }

    public HttpResponse post(HttpRequestDetails requestDetails, HttpHeaders httpHeaders, String json) {
        HttpPost httpPost = new HttpPost(requestDetails.getUri());
        StringEntity requestEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(readTimeout)
                .setSocketTimeout(connectTimeout)
                .build();
        if (connectionManager == null) {
            connectionManager = new PoolingHttpClientConnectionManager();
        }
        closeableHttpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connectionManager)
                .build();
        requestDetails.addDetailsTo(httpPost);
        httpHeaders.addTo(httpPost);
        httpPost.setEntity(requestEntity);

        try {
            return closeableHttpClient.execute(httpPost);
        } catch (IOException e) {
            throw new WebClientsException("Error executing request", e);
        }
    }

    void closeConnection() {
        if (closeableHttpClient != null) {
            if (connectionManager == null) {
                try {
                    closeableHttpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public HttpClientInternal createNew() {
        return new HttpClientInternal(connectTimeout, readTimeout);
    }
}
