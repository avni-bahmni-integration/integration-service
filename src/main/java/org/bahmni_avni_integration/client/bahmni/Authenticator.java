package org.bahmni_avni_integration.client.bahmni;

import java.net.URI;

public interface Authenticator {
    public HttpRequestDetails getRequestDetails(URI uri);

    public HttpRequestDetails refreshRequestDetails(URI uri);
}
