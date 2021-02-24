package org.bahmni_avni_integration.worker.bahmni;

import org.bahmni_avni_integration.client.OpenMRSWebClient;
import org.bahmni_avni_integration.config.OpenMRSAtomFeedPropertiesFactory;
import org.bahmni_avni_integration.domain.Constants;
import org.bahmni_avni_integration.worker.bahmni.atomfeedworker.OpenMrsEncounterEventWorker;
import org.bahmni_avni_integration.worker.bahmni.atomfeedworker.OpenMrsPatientEventWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Component
public class BahmniEncounterWorker extends BaseBahmniWorker {
    private final OpenMrsEncounterEventWorker eventWorker;
    @Value("${bahmni.feed.patient}")
    private String patientFeedLink;

    @Autowired
    public BahmniEncounterWorker(PlatformTransactionManager transactionManager, DataSource dataSource, OpenMRSWebClient openMRSWebClient, OpenMRSAtomFeedPropertiesFactory atomFeedPropertiesFactory, OpenMrsEncounterEventWorker eventWorker) {
        super(transactionManager, dataSource, openMRSWebClient, atomFeedPropertiesFactory);
        this.eventWorker = eventWorker;
    }

    public void processEncounters(Constants constants) {
        eventWorker.setConstants(constants);
        process(patientFeedLink, eventWorker);
    }
}