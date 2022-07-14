package org.avni_integration_service.avni.repository;

import org.avni_integration_service.avni.client.AvniHttpClient;
import org.avni_integration_service.avni.domain.EncountersResponse;
import org.avni_integration_service.avni.domain.GeneralEncounter;
import org.avni_integration_service.avni.domain.GeneralEncountersResponse;
import org.avni_integration_service.util.FormatAndParseUtil;
import org.avni_integration_service.util.ObjectJsonMapper;
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
                "size", "10");
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

    public GeneralEncounter delete(String deletedEntity) {
        String json = null;
        HashMap<String, String> queryParams = new HashMap<>();
        ResponseEntity<GeneralEncounter> responseEntity = avniHttpClient.delete(String.format("/api/encounters/%s", deletedEntity), queryParams, json, GeneralEncounter.class);
        return responseEntity.getBody();
    }
}
