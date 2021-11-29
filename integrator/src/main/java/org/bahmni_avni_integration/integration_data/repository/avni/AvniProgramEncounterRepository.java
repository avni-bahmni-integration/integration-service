package org.bahmni_avni_integration.integration_data.repository.avni;

import org.bahmni_avni_integration.client.AvniHttpClient;
import org.bahmni_avni_integration.contract.avni.ProgramEncounter;
import org.bahmni_avni_integration.contract.avni.ProgramEncountersResponse;
import org.bahmni_avni_integration.integration_data.util.FormatAndParseUtil;
import org.bahmni_avni_integration.util.ObjectJsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class AvniProgramEncounterRepository extends BaseAvniRepository {
    @Autowired
    private AvniHttpClient avniHttpClient;

    public ProgramEncountersResponse getProgramEncounters(Date lastModifiedDateTime) {
        Map<String, String> queryParams = Map.of(
                "lastModifiedDateTime", FormatAndParseUtil.toISODateTimeString(lastModifiedDateTime),
                "size", "10");
        ResponseEntity<ProgramEncountersResponse> responseEntity = avniHttpClient.get("/api/programEncounters", queryParams, ProgramEncountersResponse.class);
        return responseEntity.getBody();
    }

    public ProgramEncounter getProgramEncounter(String id) {
        ResponseEntity<ProgramEncounter> responseEntity = avniHttpClient.get(String.format("/api/programEncounter/%s", id), ProgramEncounter.class);
        return responseEntity.getBody();
    }

    public ProgramEncounter get(HashMap<String, Object> concepts) {
        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("concepts", ObjectJsonMapper.writeValueAsString(concepts));
        ResponseEntity<ProgramEncountersResponse> responseEntity = avniHttpClient.get("/api/programEncounters", queryParams, ProgramEncountersResponse.class);
        return pickAndExpectOne(responseEntity.getBody().getContent());
    }

    public ProgramEncounter get(String encounterType, Map<String, Object> concepts) {
        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("concepts", ObjectJsonMapper.writeValueAsString(concepts));
        queryParams.put("encounterType", encounterType);
        ResponseEntity<ProgramEncountersResponse> responseEntity = avniHttpClient.get("/api/programEncounters", queryParams, ProgramEncountersResponse.class);
        return pickAndExpectOne(responseEntity.getBody().getContent());
    }

    public ProgramEncounter create(ProgramEncounter encounter) {
        ResponseEntity<ProgramEncounter> responseEntity = avniHttpClient.post("/api/programEncounter", encounter, ProgramEncounter.class);
        return responseEntity.getBody();
    }

    public ProgramEncounter update(String id, ProgramEncounter encounter) {
        ResponseEntity<ProgramEncounter> responseEntity = avniHttpClient.put(String.format("/api/programEncounter/%s", id), encounter, ProgramEncounter.class);
        return responseEntity.getBody();
    }
}
