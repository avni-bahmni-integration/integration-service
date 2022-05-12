package org.avni_integration_service.worker.bahmni;

import org.avni_integration_service.bahmni.client.OpenMRSWebClient;
import org.avni_integration_service.bahmni.client.config.OpenMRSAtomFeedPropertiesFactory;
import org.avni_integration_service.integration_data.domain.Constants;
import org.avni_integration_service.worker.bahmni.atomfeedworker.PatientEventWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Component
public class PatientWorker extends BaseBahmniWorker implements PatientsProcessor {
    @Value("${bahmni.feed.patient}")
    private String patientFeedLink;
    private final PatientEventWorker eventWorker;

    @Autowired
    public PatientWorker(PlatformTransactionManager transactionManager, DataSource dataSource, OpenMRSWebClient openMRSWebClient, OpenMRSAtomFeedPropertiesFactory atomFeedPropertiesFactory, PatientEventWorker eventWorker) {
        super(transactionManager, dataSource, openMRSWebClient, atomFeedPropertiesFactory);
        this.eventWorker = eventWorker;
    }

    public void processPatients() {
        process(patientFeedLink, eventWorker);
    }

    public void cacheRunImmutables(Constants constants) {
        eventWorker.cacheRunImmutables(constants);
    }
}
