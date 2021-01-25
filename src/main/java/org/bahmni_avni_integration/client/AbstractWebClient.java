package org.bahmni_avni_integration.client;

import org.apache.log4j.Logger;
import org.bahmni.webclients.ClientCookies;
import org.bahmni.webclients.ConnectionDetails;
import org.bahmni.webclients.HttpClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public abstract class AbstractWebClient {
    protected HttpClient httpClient;
    protected ConnectionDetails connectionDetails;

    public String get(URI uri) {
        return httpClient.get(uri);
    }


    public <T> T get(String uri, Class<T> klass) throws IOException {
        return httpClient.get(uri,klass);
    }

    protected abstract ConnectionDetails connectionDetails(OpenERPAtomFeedProperties properties);

    protected abstract Logger getLogger();

    public ClientCookies getCookies() {
        try {
            return httpClient.getCookies(new URI(connectionDetails.getAuthUrl()));
        } catch (URISyntaxException e) {
            getLogger().error("Unable to get Cookies", e);
        }
        return new ClientCookies();
    }
}
