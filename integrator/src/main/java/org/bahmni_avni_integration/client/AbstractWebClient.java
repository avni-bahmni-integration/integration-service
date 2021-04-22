package org.bahmni_avni_integration.client;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.client.bahmni.ClientCookies;
import org.bahmni_avni_integration.client.bahmni.ConnectionDetails;
import org.bahmni_avni_integration.client.bahmni.HttpClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public abstract class AbstractWebClient {
    protected HttpClient httpClient;
    protected ConnectionDetails connectionDetails;
    private static Logger logger = Logger.getLogger(AbstractWebClient.class);

    public String get(URI uri) {
        logger.debug("GET %s".formatted(uri.toString()));
        return httpClient.get(uri);
    }

    public String get(String url) {
        return this.get(URI.create(url));
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
