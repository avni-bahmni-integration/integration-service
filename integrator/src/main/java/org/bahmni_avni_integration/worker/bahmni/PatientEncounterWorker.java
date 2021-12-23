package org.bahmni_avni_integration.worker.bahmni;

import org.bahmni_avni_integration.client.OpenMRSWebClient;
import org.bahmni_avni_integration.config.OpenMRSAtomFeedPropertiesFactory;
import org.bahmni_avni_integration.integration_data.domain.Constants;
import org.bahmni_avni_integration.worker.bahmni.atomfeedworker.PatientEncounterEventWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Component
public class PatientEncounterWorker extends BaseBahmniWorker implements PatientEncountersProcessor {
    private final PatientEncounterEventWorker eventWorker;
    @Value("${bahmni.feed.encounter}")
    private String encounterFeedLink;

    @Autowired
    public PatientEncounterWorker(PlatformTransactionManager transactionManager, DataSource dataSource, OpenMRSWebClient openMRSWebClient, OpenMRSAtomFeedPropertiesFactory atomFeedPropertiesFactory, PatientEncounterEventWorker eventWorker) {
        super(transactionManager, dataSource, openMRSWebClient, atomFeedPropertiesFactory);
        this.eventWorker = eventWorker;
    }

    public void processEncounters() {
        process(encounterFeedLink, eventWorker);
    }

    public void cacheRunImmutables(Constants constants) {
        eventWorker.cacheRunImmutables(constants);
    }
}
