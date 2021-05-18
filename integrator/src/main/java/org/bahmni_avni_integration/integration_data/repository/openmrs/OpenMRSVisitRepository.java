package org.bahmni_avni_integration.integration_data.repository.openmrs;

import com.fasterxml.jackson.core.type.TypeReference;
import org.bahmni_avni_integration.client.OpenMRSWebClient;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSSaveVisit;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSUuidHolder;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSVisit;
import org.bahmni_avni_integration.contract.bahmni.SearchResults;
import org.bahmni_avni_integration.util.ObjectJsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class OpenMRSVisitRepository extends BaseOpenMRSRepository {

    @Autowired
    public OpenMRSVisitRepository(OpenMRSWebClient openMRSWebClient) {
        super(openMRSWebClient);
    }

    public OpenMRSVisit getVisit(String patientUuid, String locationUuid) {
        String json = openMRSWebClient.get("%s?patient=%s&location=%s&v=full".formatted(resourcePath(), patientUuid, locationUuid));
        SearchResults<OpenMRSVisit> searchResults = ObjectJsonMapper.readValue(json, new TypeReference<SearchResults<OpenMRSVisit>>() {
        });
        return pickAndExpectOne(searchResults, String.format("%s-%s", patientUuid, locationUuid));
    }

    public OpenMRSVisit createVisit(OpenMRSSaveVisit openMRSSaveVisit) {
        String json = ObjectJsonMapper.writeValueAsString(openMRSSaveVisit);
        String outputJson = openMRSWebClient.post(resourcePath(), json);
        return ObjectJsonMapper.readValue(outputJson, OpenMRSVisit.class);
    }

    public void deleteVisit(String visitUuid) {
        openMRSWebClient.delete(URI.create("%s/%s?purge=true".formatted(resourcePath(), visitUuid)));
    }

    private String resourcePath() {
        return getResourcePath("visit");
    }

}