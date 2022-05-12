package org.avni_integration_service.bahmni.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import org.avni_integration_service.bahmni.client.OpenMRSWebClient;
import org.avni_integration_service.bahmni.contract.OpenMRSConcept;
import org.avni_integration_service.bahmni.contract.SearchResults;
import org.avni_integration_service.util.ObjectJsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpenMRSConceptRepository extends BaseOpenMRSRepository {

    @Autowired
    public OpenMRSConceptRepository(OpenMRSWebClient openMRSWebClient) {
        super(openMRSWebClient);
    }

    public OpenMRSConcept getConceptByName(String name) {
        String json = openMRSWebClient.get(getFullPath(String.format("concept?q=%s", encode(name))));
        SearchResults<OpenMRSConcept> searchResults = ObjectJsonMapper.readValue(json, new TypeReference<SearchResults<OpenMRSConcept>>(){});
        return searchResults.getResults().stream().filter(openMRSConcept -> openMRSConcept.getDisplay().equals(name)).findFirst().orElse(null);
    }
}
