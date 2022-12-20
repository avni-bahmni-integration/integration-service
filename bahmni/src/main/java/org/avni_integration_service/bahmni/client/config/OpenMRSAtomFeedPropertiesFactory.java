package org.avni_integration_service.bahmni.client.config;

import org.ict4h.atomfeed.client.AtomFeedProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OpenMRSAtomFeedPropertiesFactory {
    @Value("${bahmni.feed.connect.timeout}")
    private int bahmniFeedConnectTimeout;

    @Value("${bahmni.feed.item.read.timeout}")
    private int bahmniFeedReadTimeout;

    @Value("${bahmni.feed.max.failed.events}")
    private int bahmniFeedMaxFailedEvents;

    public AtomFeedProperties getProperties() {
        AtomFeedProperties feedProperties = new AtomFeedProperties();
        feedProperties.setConnectTimeout(bahmniFeedConnectTimeout);
        feedProperties.setReadTimeout(bahmniFeedReadTimeout);
        feedProperties.setMaxFailedEvents(bahmniFeedMaxFailedEvents);
        return feedProperties;
    }
}
