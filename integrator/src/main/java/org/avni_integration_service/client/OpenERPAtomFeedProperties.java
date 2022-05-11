package org.avni_integration_service.client;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;

@Component
public class OpenERPAtomFeedProperties implements OpenERPProperties {
//todo - delete all openerp name
    private static Logger logger = Logger.getLogger(OpenERPAtomFeedProperties.class);

    @Value("${openerp.connectionTimeoutInMilliseconds}")
    private String openErpConTimeOut;

    @Override
    public int getConnectionTimeoutInMilliseconds() {
        return Integer.parseInt(openErpConTimeOut);
    }

    @Value("${openerp.replyTimeoutInMilliseconds}")
    private String openErpReplyTimeOut;

    @Override
    public int getReplyTimeoutInMilliseconds() {
        return Integer.parseInt(openErpReplyTimeOut);
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
        logger.debug("**************** DEBUG OpenERPAtomFeedProperties ************************ ");
        HashMap<String, String> properties = getInfo();
        for (String s : properties.keySet()) {
            logger.debug(String.format("%s=%s",s, properties.get(s)));

        }
        logger.debug("**************** DEBUG OpenERPAtomFeedProperties ************************ ");
    }



    private HashMap<String, String> getInfo() {
        HashMap<String, String> values = new HashMap<>();
        values.put("openerp.connectionTimeoutInMilliseconds",openErpConTimeOut );
        values.put("openerp.replyTimeoutInMilliseconds",openErpReplyTimeOut );
        values.put("openmrs.auth.uri",openmrsAuthUri );
        values.put("openmrs.user",openmrsUser );
        return values;
    }


}
