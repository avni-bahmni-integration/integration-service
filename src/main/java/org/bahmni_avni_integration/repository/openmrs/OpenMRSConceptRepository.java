package org.bahmni_avni_integration.repository.openmrs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.bahmni_avni_integration.client.OpenMRSWebClient;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSConcept;
import org.bahmni_avni_integration.contract.bahmni.SearchResults;
import org.bahmni_avni_integration.repository.MultipleResultsFoundException;
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
        return searchResults.getResults().stream().filter(openMRSConcept -> openMRSConcept.getDisplay().equals(name)).findFirst().orElse(null);
    }
}