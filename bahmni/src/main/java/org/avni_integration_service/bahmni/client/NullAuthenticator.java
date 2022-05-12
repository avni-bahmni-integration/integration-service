package org.avni_integration_service.bahmni.client;

import java.net.URI;

public class NullAuthenticator implements Authenticator{
    @Override
    public HttpRequestDetails getRequestDetails(URI uri) {
        return new HttpRequestDetails(uri);
    }

    @Override
    public HttpRequestDetails refreshRequestDetails(URI uri) {
        return new HttpRequestDetails(uri);
    }
}
