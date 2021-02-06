package org.bahmni_avni_integration.repository.openmrs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.bahmni_avni_integration.client.OpenMRSWebClient;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSPatient;
import org.bahmni_avni_integration.contract.bahmni.SearchResults;
import org.bahmni_avni_integration.util.ObjectJsonMapper;
import org.ict4h.atomfeed.client.domain.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;

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
        return ObjectJsonMapper.readValue(patientJSON, OpenMRSPatient.class);
    }

    public OpenMRSPatient getPatientByIdentifier(String identifier) {
        String patientJSON = openMRSWebClient.get(URI.create(String.format("%s?identifier=%s", getResourcePath("patient"), identifier)));
        SearchResults<OpenMRSPatient> searchResults = ObjectJsonMapper.readValue(patientJSON, new TypeReference<SearchResults<OpenMRSPatient>>() {});
        return pickOne(searchResults, identifier);
    }
}