package org.avni_integration_service.bahmni.worker.bahmni;

import org.avni_integration_service.bahmni.client.OpenMRSWebClient;
import org.avni_integration_service.bahmni.client.ClientCookies;
import org.avni_integration_service.bahmni.client.config.OpenMRSAtomFeedPropertiesFactory;
import org.ict4h.atomfeed.client.AtomFeedProperties;
import org.ict4h.atomfeed.client.repository.AllFeeds;
import org.ict4h.atomfeed.client.repository.jdbc.AllFailedEventsJdbcImpl;
import org.ict4h.atomfeed.client.repository.jdbc.AllMarkersJdbcImpl;
import org.ict4h.atomfeed.client.service.AtomFeedClient;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.ict4h.atomfeed.server.transaction.AtomFeedSpringTransactionSupport;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

public abstract class BaseBahmniWorker {
    private final AtomFeedProperties feedProperties;
    private final AllMarkersJdbcImpl allMarkers;
    private final AllFailedEventsJdbcImpl allFailedEvents;
    private final AtomFeedSpringTransactionSupport transactionManagerImpl;
    private final OpenMRSWebClient openMRSWebClient;

    protected BaseBahmniWorker(PlatformTransactionManager transactionManager, DataSource dataSource, OpenMRSWebClient openMRSWebClient, OpenMRSAtomFeedPropertiesFactory atomFeedPropertiesFactory) {
        this.openMRSWebClient = openMRSWebClient;
        feedProperties = atomFeedPropertiesFactory.getProperties();
        transactionManagerImpl = new AtomFeedSpringTransactionSupport(
                transactionManager,
                dataSource
        );
        allMarkers = new AllMarkersJdbcImpl(transactionManagerImpl);
        allFailedEvents = new AllFailedEventsJdbcImpl(transactionManagerImpl);
    }

    protected void process(String feedLink, EventWorker eventWorker) {
        try {
            URI uri = new URI(feedLink);
            ClientCookies cookies = openMRSWebClient.getCookies();
            AllFeeds allFeeds = new AllFeeds(feedProperties, cookies);
            AtomFeedClient atomFeedClient = new AtomFeedClient(
                    allFeeds,
                    allMarkers,
                    allFailedEvents,
                    feedProperties,
                    transactionManagerImpl,
                    uri,
                    eventWorker);
            atomFeedClient.processEvents();
        } catch (URISyntaxException e) {
            throw new RuntimeException("error for uri:" + feedLink, e);
        }
    }
}
