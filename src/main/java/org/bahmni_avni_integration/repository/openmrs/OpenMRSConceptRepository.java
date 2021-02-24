package org.bahmni_avni_integration.repository.openmrs;

import com.fasterxml.jackson.core.type.TypeReference;
import org.bahmni_avni_integration.client.OpenMRSWebClient;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSConcept;
import org.bahmni_avni_integration.contract.bahmni.SearchResults;
import org.bahmni_avni_integration.util.ObjectJsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpenMRSConceptRepository extends BaseOpenMRSRepository {
    private OpenMRSWebClient openMRSWebClient;

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