package org.bahmni_avni_integration.repository.avni;

import org.bahmni_avni_integration.client.AvniHttpClient;
import org.bahmni_avni_integration.contract.avni.GeneralEncounter;
import org.bahmni_avni_integration.contract.avni.EncountersResponse;
import org.bahmni_avni_integration.util.ObjectJsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class AvniEncounterRepository extends BaseAvniRepository {
    @Autowired
    private AvniHttpClient avniHttpClient;

    public GeneralEncounter getEncounter(HashMap<String, Object> concepts) {
        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("concepts", ObjectJsonMapper.writeValueAsString(concepts));
        ResponseEntity<EncountersResponse> responseEntity = avniHttpClient.get("/api/encounters", queryParams, EncountersResponse.class);
        return pickAndExpectOne(responseEntity.getBody().getContent());
    }

    public GeneralEncounter create(GeneralEncounter encounter) {
        ResponseEntity<GeneralEncounter> responseEntity = avniHttpClient.post("/api/encounter", encounter, GeneralEncounter.class);
        return responseEntity.getBody();
    }

    public GeneralEncounter update(String id, GeneralEncounter encounter) {
        ResponseEntity<GeneralEncounter> responseEntity = avniHttpClient.put(String.format("/api/encounter/%s", id), encounter, GeneralEncounter.class);
        return responseEntity.getBody();
    }
}