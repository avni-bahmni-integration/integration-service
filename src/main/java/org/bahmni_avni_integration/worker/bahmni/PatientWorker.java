package org.bahmni_avni_integration.worker.bahmni;

import org.bahmni_avni_integration.client.OpenMRSWebClient;
import org.bahmni_avni_integration.config.OpenMRSAtomFeedPropertiesFactory;
import org.bahmni_avni_integration.domain.Constants;
import org.bahmni_avni_integration.worker.bahmni.atomfeedworker.OpenMrsPatientEventWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Component
public class PatientWorker extends BaseBahmniWorker {
    @Value("${bahmni.feed.patient}")
    private String patientFeedLink;
    private final OpenMrsPatientEventWorker eventWorker;

    @Autowired
    public PatientWorker(PlatformTransactionManager transactionManager, DataSource dataSource, OpenMRSWebClient openMRSWebClient, OpenMRSAtomFeedPropertiesFactory atomFeedPropertiesFactory, OpenMrsPatientEventWorker eventWorker) {
        super(transactionManager, dataSource, openMRSWebClient, atomFeedPropertiesFactory);
        this.eventWorker = eventWorker;
    }

    public void processPatients(Constants constants) {
        process(patientFeedLink, eventWorker);
    }
}