package org.bahmni_avni_integration.repository.openmrs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.bahmni_avni_integration.client.OpenMRSWebClient;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSConcept;
import org.bahmni_avni_integration.contract.bahmni.SearchResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;

import static org.bahmni.webclients.ObjectMapperRepository.objectMapper;

@Component
public class OpenMRSConceptRepository extends BaseOpenMRSRepository {
    @Autowired
    private OpenMRSWebClient openMRSWebClient;

    public OpenMRSConcept getConceptByName(String name) throws JsonProcessingException {
        String json = openMRSWebClient.get(URI.create(getFullPath(String.format("concept?q=%s", encode(name)))));
        SearchResults<OpenMRSConcept> searchResults = objectMapper.readValue(json, new TypeReference<SearchResults<OpenMRSConcept>>(){});
        if (searchResults.getResults().size() == 0) return null;
        if (searchResults.getResults().size() > 1) throw new RuntimeException(String.format("More than concepts found with name: %s", name));
        return searchResults.getResults().get(0);
    }
}