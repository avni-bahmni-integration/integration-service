package org.avni_integration_service.bahmni.client;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;

@Component
public class BahmniAtomFeedProperties implements IBahmniAtomFeedProperties {
    private static final Logger logger = Logger.getLogger(BahmniAtomFeedProperties.class);

    @Value("${bahmni.connectionTimeoutInMilliseconds}")
    private String bahmniConnectionTimeOut;

    @Override
    public int getConnectionTimeoutInMilliseconds() {
        return Integer.parseInt(bahmniConnectionTimeOut);
    }

    @Value("${bahmni.replyTimeoutInMilliseconds}")
    private String bahmniReplyTimeOut;

    @Override
    public int getReplyTimeoutInMilliseconds() {
        return Integer.parseInt(bahmniReplyTimeOut);
    }

    @Value("${openmrs.auth.uri}")
    private String openmrsAuthUri;

    public String getAuthenticationURI() {
        return openmrsAuthUri;
    }

    @Value("${openmrs.user}")
    private String openmrsUser;

    public String getOpenMRSUser() {
        return openmrsUser;
    }

    @Value("${openmrs.password}")
    private String openmrsPwd;

    public String getOpenMRSPassword() {
        return openmrsPwd;
    }

    @PostConstruct
    private void debug() {
        logger.debug("**************** DEBUG Bahmni AtomFeed Properties ************************ ");
        HashMap<String, String> properties = getInfo();
        for (String s : properties.keySet()) {
            logger.debug(String.format("%s=%s",s, properties.get(s)));

        }
        logger.debug("**************** DEBUG Bahmni AtomFeed Properties ************************ ");
    }



    private HashMap<String, String> getInfo() {
        HashMap<String, String> values = new HashMap<>();
        values.put("bahmni.connectionTimeoutInMilliseconds", bahmniConnectionTimeOut);
        values.put("bahmni.replyTimeoutInMilliseconds", bahmniReplyTimeOut);
        values.put("openmrs.auth.uri",openmrsAuthUri );
        values.put("openmrs.user",openmrsUser );
        return values;
    }


}
