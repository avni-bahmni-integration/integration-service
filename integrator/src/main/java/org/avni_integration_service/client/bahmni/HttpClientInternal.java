package org.avni_integration_service.client.bahmni;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import java.io.IOException;

public class HttpClientInternal {
    private int connectTimeout;
    private int readTimeout;
    private DefaultHttpClient defaultHttpClient;
    private ClientConnectionManager connectionManager;

    HttpClientInternal(int connectionTimeout, int readTimeout) {
        this.connectTimeout = connectionTimeout;
        this.readTimeout = readTimeout;
    }

    HttpClientInternal(int connectionTimeout, int readTimeout, ClientConnectionManager connectionManager) {
        this.connectTimeout = connectionTimeout;
        this.readTimeout = readTimeout;
        this.connectionManager = connectionManager;
    }


    public HttpResponse get(HttpRequestDetails requestDetails) {
        return get(requestDetails, new HttpHeaders());
    }

    public HttpResponse get(HttpRequestDetails requestDetails, HttpHeaders httpHeaders) {
        defaultHttpClient = (connectionManager == null) ? new DefaultHttpClient() : new DefaultHttpClient(connectionManager);
        defaultHttpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, readTimeout);
        defaultHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectTimeout);

        HttpGet httpGet = new HttpGet(requestDetails.getUri());
        requestDetails.addDetailsTo(httpGet);
        httpHeaders.addTo(httpGet);

        try {
            return defaultHttpClient.execute(httpGet);
        } catch (IOException e) {
            throw new WebClientsException(e);
        }
    }

    public HttpResponse post(HttpRequestDetails requestDetails, HttpHeaders httpHeaders, String json) {
        defaultHttpClient = (connectionManager == null) ? new DefaultHttpClient() : new DefaultHttpClient(connectionManager);
        defaultHttpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, readTimeout);
        defaultHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectTimeout);

        HttpPost httpPost = new HttpPost(requestDetails.getUri());
        requestDetails.addDetailsTo(httpPost);
        httpHeaders.addTo(httpPost);
        StringEntity requestEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
        httpPost.setEntity(requestEntity);

        try {
            return defaultHttpClient.execute(httpPost);
        } catch (IOException e) {
            throw new WebClientsException(e);
        }

    }

    void closeConnection() {
        if (defaultHttpClient != null){
            if(connectionManager == null){
                defaultHttpClient.getConnectionManager().shutdown();
            }else{
                defaultHttpClient.getConnectionManager().closeExpiredConnections();
            }
        }
    }

    public HttpClientInternal createNew() {
        return new HttpClientInternal(connectTimeout, readTimeout);
    }

    public void delete(HttpRequestDetails requestDetails, HttpHeaders httpHeaders) {
        defaultHttpClient = (connectionManager == null) ? new DefaultHttpClient() : new DefaultHttpClient(connectionManager);
        defaultHttpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, readTimeout);
        defaultHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectTimeout);

        HttpDelete httpDelete = new HttpDelete(requestDetails.getUri());
        requestDetails.addDetailsTo(httpDelete);
        httpHeaders.addTo(httpDelete);
        try {
            defaultHttpClient.execute(httpDelete);
        } catch (IOException e) {
            throw new WebClientsException(e);
        }
    }
}
