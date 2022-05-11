package org.avni_integration_service.client.bahmni;

import java.net.URI;

public interface Authenticator {
    public HttpRequestDetails getRequestDetails(URI uri);

    public HttpRequestDetails refreshRequestDetails(URI uri);
}
