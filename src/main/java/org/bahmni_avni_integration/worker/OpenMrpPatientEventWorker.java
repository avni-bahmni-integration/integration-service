package org.bahmni_avni_integration.worker;

import org.bahmni_avni_integration.client.OpenMRSWebClient;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSPatient;
import org.bahmni_avni_integration.domain.OpenMRSPatientMapper;
import org.bahmni_avni_integration.util.ObjectMapperRepository;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class OpenMrpPatientEventWorker implements EventWorker {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OpenMRSWebClient webClient;

    @Value("${openmrs.uri.prefix}")
    private String urlPrefix;


    @Override
    public void process(Event event) {
        String content = event.getContent();
        logger.info(event.toString());
        logger.info(content);

        String patientJSON = webClient.get(URI.create(urlPrefix + content));

        OpenMRSPatientMapper openMRSPatientMapper = new OpenMRSPatientMapper(ObjectMapperRepository.objectMapper);
        try {
            OpenMRSPatient openMRSPatient = openMRSPatientMapper.map(patientJSON);
            logger.info("Patient data", openMRSPatient);
        } catch (Exception e) {
            logger.error("OpenMrpPatientEventWorker failed", e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public void cleanUp(Event event) {
    }

}
