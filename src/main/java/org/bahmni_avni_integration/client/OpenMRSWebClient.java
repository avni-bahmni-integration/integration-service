package org.bahmni_avni_integration.client;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.client.bahmni.ConnectionDetails;
import org.bahmni_avni_integration.client.bahmni.HttpClient;
import org.bahmni_avni_integration.client.bahmni.openmrs.OpenMRSLoginAuthenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpenMRSWebClient extends AbstractWebClient {

    private static Logger logger = Logger.getLogger(OpenMRSWebClient.class);


    public OpenMRSWebClient(@Autowired OpenERPAtomFeedProperties properties) {
        connectionDetails = connectionDetails(properties);
        httpClient = new HttpClient(connectionDetails, new OpenMRSLoginAuthenticator(connectionDetails));
    }

    @Override
    protected ConnectionDetails connectionDetails(OpenERPAtomFeedProperties properties) {
        return new ConnectionDetails(properties.getAuthenticationURI(),
                properties.getOpenMRSUser(),
                properties.getOpenMRSPassword(),
                properties.getConnectionTimeoutInMilliseconds(),
                properties.getReplyTimeoutInMilliseconds());
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    public void post(String resourcePath, String json) {
    }
}
