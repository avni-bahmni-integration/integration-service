package org.avni_integration_service.integration_data.repository.openmrs;

import com.fasterxml.jackson.core.type.TypeReference;
import org.avni_integration_service.client.OpenMRSWebClient;
import org.avni_integration_service.contract.bahmni.*;
import org.avni_integration_service.util.ObjectJsonMapper;
import org.ict4h.atomfeed.client.domain.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class OpenMRSPatientRepository extends BaseOpenMRSRepository {
    @Autowired
    public OpenMRSPatientRepository(OpenMRSWebClient openMRSWebClient) {
        super(openMRSWebClient);
    }

    public OpenMRSPatient getPatient(Event event) {
        String patientJSON = getUnderlyingResourceJson(event);
        return ObjectJsonMapper.readValue(patientJSON, OpenMRSPatient.class);
    }

    public OpenMRSPatient getPatientByIdentifier(String identifier) {
        String patientJSON = openMRSWebClient.get(URI.create(String.format("%s?identifier=%s&v=full", getResourcePath("patient"), identifier)));
        SearchResults<OpenMRSPatient> searchResults = ObjectJsonMapper.readValue(patientJSON, new TypeReference<SearchResults<OpenMRSPatient>>() {
        });
        // story-todo do full run after changing it
        return pickAndExpectOne(searchResults.removeDuplicates(), identifier);
    }

    public OpenMRSPatient createPatient(OpenMRSSavePatient openMRSSavePatient) {
        String json = ObjectJsonMapper.writeValueAsString(openMRSSavePatient);
        String outputJson = openMRSWebClient.post(getResourcePath("patient"), json);
        return ObjectJsonMapper.readValue(outputJson, OpenMRSPatient.class);
    }

    public OpenMRSPatient getPatient(String patientUuid) {
        String patientJSON = openMRSWebClient.get(URI.create(String.format("%s/%s/patient/%s?v=full", urlPrefix, BaseOpenMRSRepository.OPENMRS_BASE_PATH, patientUuid)));
        return ObjectJsonMapper.readValue(patientJSON, OpenMRSPatient.class);
    }
}
