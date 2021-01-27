package org.bahmni_avni_integration.repository.openmrs;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.bahmni_avni_integration.client.OpenMRSWebClient;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSPatient;
import org.ict4h.atomfeed.client.domain.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;

import static org.bahmni.webclients.ObjectMapperRepository.objectMapper;

@Component
public class OpenMRSPatientRepository extends BaseOpenMRSRepository {
    private OpenMRSWebClient openMRSWebClient;

    @Autowired
    public OpenMRSPatientRepository(OpenMRSWebClient openMRSWebClient) {
        this.openMRSWebClient = openMRSWebClient;
    }

    public OpenMRSPatient getPatient(Event event) throws JsonProcessingException {
        String content = event.getContent();
        String patientJSON = openMRSWebClient.get(URI.create(urlPrefix + content));
        return objectMapper.readValue(patientJSON, OpenMRSPatient.class);
    }

    public OpenMRSPatient getPatientByIdentifier(String identifier) throws JsonProcessingException {
        String patientJSON = openMRSWebClient.get(URI.create(String.format("%s%s", getResourcePath("patient"), identifier)));
        return objectMapper.readValue(patientJSON, OpenMRSPatient.class);
    }
}