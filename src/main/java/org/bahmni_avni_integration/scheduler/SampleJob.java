package org.bahmni_avni_integration.scheduler;

import org.bahmni_avni_integration.client.OpenMRSWebClient;
import org.bahmni_avni_integration.client.AvniHttpClient;
import org.bahmni_avni_integration.worker.OpenMrpPatientEventWorker;
import org.bahmni.webclients.ClientCookies;
import org.bahmni_avni_integration.worker.avni.SubjectWorker;
import org.ict4h.atomfeed.client.AtomFeedProperties;
import org.ict4h.atomfeed.client.repository.AllFailedEvents;
import org.ict4h.atomfeed.client.repository.AllFeeds;
import org.ict4h.atomfeed.client.repository.AllMarkers;
import org.ict4h.atomfeed.client.repository.jdbc.AllFailedEventsJdbcImpl;
import org.ict4h.atomfeed.client.repository.jdbc.AllMarkersJdbcImpl;
import org.ict4h.atomfeed.client.service.AtomFeedClient;
import org.ict4h.atomfeed.server.transaction.AtomFeedSpringTransactionSupport;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

@Component
public class SampleJob implements Job {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    AvniHttpClient avniHttpClient;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private OpenMRSWebClient openMRSWebClient;

    @Autowired
    private OpenMrpPatientEventWorker eventWorker;

    @Autowired
    private SubjectWorker subjectWorker;


    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("Job ** {} ** fired @ {}", context.getJobDetail().getKey().getName(), context.getFireTime());

        try {
            syncDataFromAvniToBahmni();
            syncDataFromBahmniToAvni();
        } catch (Exception e) {
            logger.error("Error calling API", e);
        }

        logger.info("Next job scheduled @ {}", context.getNextFireTime());
    }

    private void syncDataFromAvniToBahmni() {
        subjectWorker.processSubjects();
    }

    private void syncDataFromBahmniToAvni() {
        AtomFeedProperties feedProperties = new AtomFeedProperties();
        feedProperties.setConnectTimeout(500);
        feedProperties.setReadTimeout(20000);
        feedProperties.setMaxFailedEvents(1000);
        ClientCookies cookies = openMRSWebClient.getCookies();

        AllFeeds allFeeds = new AllFeeds(feedProperties, cookies);

        AtomFeedSpringTransactionSupport transactionManagerImpl = new AtomFeedSpringTransactionSupport(
                transactionManager,
                dataSource
        );

        AllMarkers allMarkers = new AllMarkersJdbcImpl(transactionManagerImpl);
        AllFailedEvents allFailedEvents = new AllFailedEventsJdbcImpl(transactionManagerImpl);
        String feedUri = "http://143.110.188.91:8050/openmrs/ws/atomfeed/patient/1";

        try {
            AtomFeedClient atomFeedClient = new AtomFeedClient(
                    allFeeds,
                    allMarkers,
                    allFailedEvents,
                    feedProperties,
                    transactionManagerImpl,
                    new URI(feedUri),
                    eventWorker);
            atomFeedClient.processEvents();
        } catch (URISyntaxException e) {
            throw new RuntimeException("error for uri:" + feedUri, e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
