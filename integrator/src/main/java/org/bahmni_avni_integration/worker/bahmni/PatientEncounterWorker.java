package org.bahmni_avni_integration.worker.bahmni;

import org.bahmni_avni_integration.client.OpenMRSWebClient;
import org.bahmni_avni_integration.config.OpenMRSAtomFeedPropertiesFactory;
import org.bahmni_avni_integration.integration_data.domain.Constants;
import org.bahmni_avni_integration.service.MappingMetaDataService;
import org.bahmni_avni_integration.worker.bahmni.atomfeedworker.PatientEncounterEventWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Component
public class PatientEncounterWorker extends BaseBahmniWorker {
    private final PatientEncounterEventWorker eventWorker;
    private MappingMetaDataService mappingMetaDataService;
    @Value("${bahmni.feed.patient}")
    private String patientFeedLink;

    @Autowired
    public PatientEncounterWorker(PlatformTransactionManager transactionManager, DataSource dataSource, OpenMRSWebClient openMRSWebClient, OpenMRSAtomFeedPropertiesFactory atomFeedPropertiesFactory, PatientEncounterEventWorker eventWorker, MappingMetaDataService mappingMetaDataService) {
        super(transactionManager, dataSource, openMRSWebClient, atomFeedPropertiesFactory);
        this.eventWorker = eventWorker;
        this.mappingMetaDataService = mappingMetaDataService;
    }

    public void processEncounters(Constants constants) {
        eventWorker.setConstants(constants);
        eventWorker.setMetaData(mappingMetaDataService.getForBahmniEncounterToAvniEncounter());
        process(patientFeedLink, eventWorker);
    }
}