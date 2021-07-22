package org.bahmni_avni_integration.integration_data.repository.avni;

import org.bahmni_avni_integration.client.AvniHttpClient;
import org.bahmni_avni_integration.contract.avni.GeneralEncounter;
import org.bahmni_avni_integration.contract.avni.EncountersResponse;
import org.bahmni_avni_integration.contract.avni.GeneralEncountersResponse;
import org.bahmni_avni_integration.integration_data.util.FormatAndParseUtil;
import org.bahmni_avni_integration.util.ObjectJsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

    public GeneralEncounter getEncounter(String encounterType, Map<String, Object> concepts) {
        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("concepts", ObjectJsonMapper.writeValueAsString(concepts));
        queryParams.put("encounterType", encounterType);
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

    public GeneralEncountersResponse getGeneralEncounters(Date lastModifiedDateTime) {
        Map<String, String> queryParams = Map.of(
                "lastModifiedDateTime", FormatAndParseUtil.toISODateTimeString(lastModifiedDateTime),
                "size", "100");
        ResponseEntity<GeneralEncountersResponse> responseEntity = avniHttpClient.get("/api/encounters", queryParams, GeneralEncountersResponse.class);
        return responseEntity.getBody();
    }

    public GeneralEncounter getGeneralEncounter(String id) {
        ResponseEntity<GeneralEncounter> responseEntity = avniHttpClient.get(String.format("/api/encounter/%s", id), GeneralEncounter.class);
        return responseEntity.getBody();
    }

    public GeneralEncounter get(HashMap<String, Object> concepts) {
        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("concepts", ObjectJsonMapper.writeValueAsString(concepts));
        ResponseEntity<GeneralEncountersResponse> responseEntity = avniHttpClient.get("/api/encounters", queryParams, GeneralEncountersResponse.class);
        return pickAndExpectOne(responseEntity.getBody().getContent());
    }

    public GeneralEncounter get(String encounterType, Map<String, Object> concepts) {
        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("concepts", ObjectJsonMapper.writeValueAsString(concepts));
        queryParams.put("encounterType", encounterType);
        ResponseEntity<GeneralEncountersResponse> responseEntity = avniHttpClient.get("/api/encounters", queryParams, GeneralEncountersResponse.class);
        return pickAndExpectOne(responseEntity.getBody().getContent());
    }
}
