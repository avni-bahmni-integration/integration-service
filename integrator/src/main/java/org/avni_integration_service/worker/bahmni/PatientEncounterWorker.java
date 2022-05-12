package org.avni_integration_service.worker.bahmni;

import org.avni_integration_service.bahmni.client.OpenMRSWebClient;
import org.avni_integration_service.bahmni.client.config.OpenMRSAtomFeedPropertiesFactory;
import org.avni_integration_service.integration_data.domain.Constants;
import org.avni_integration_service.worker.bahmni.atomfeedworker.PatientEncounterEventWorker;
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
