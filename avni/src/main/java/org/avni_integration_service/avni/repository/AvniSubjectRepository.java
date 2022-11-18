package org.avni_integration_service.avni.repository;

import org.avni_integration_service.avni.client.AvniHttpClient;
import org.avni_integration_service.avni.domain.*;
import org.avni_integration_service.util.FormatAndParseUtil;
import org.avni_integration_service.util.ObjectJsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

@Component
public class AvniSubjectRepository extends BaseAvniRepository {
    @Autowired
    private AvniHttpClient avniHttpClient;

    public HouseholdResponse getGroupSubjects(Date lastModifiedDateTime, String subjectType) {
        return getSubjects(lastModifiedDateTime, subjectType, "/api/groupSubjects", HouseholdResponse.class);
    }

    public SubjectsResponse getSubjects(Date lastModifiedDateTime, String subjectType) {
        return getSubjects(lastModifiedDateTime, subjectType, "/api/subjects", SubjectsResponse.class);
    }

    public <T> T getSubjects(Date lastModifiedDateTime, String subjectType, String path,
                                        Class<T> responseType) {
        String fromTime = FormatAndParseUtil.toISODateTimeString(lastModifiedDateTime);
        HashMap<String, String> queryParams = new HashMap<>(1);
        queryParams.put("lastModifiedDateTime", fromTime);
        queryParams.put("subjectType", subjectType);
        queryParams.put("size", "10");
        ResponseEntity<T> responseEntity = avniHttpClient.get(path, queryParams, responseType);
        T response = responseEntity.getBody();
        return response;
    }

    public Subject[] getSubjects(Date lastModifiedDateTime, String subjectType, HashMap<String, Object> concepts) {
        String fromTime = FormatAndParseUtil.toISODateTimeString(lastModifiedDateTime);
        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("lastModifiedDateTime", fromTime);
        queryParams.put("subjectType", subjectType);
        queryParams.put("concepts", ObjectJsonMapper.writeValueAsString(concepts));
        ResponseEntity<SubjectsResponse> responseEntity = avniHttpClient.get("/api/subjects", queryParams, SubjectsResponse.class);
        Subject[] subjects = responseEntity.getBody().getContent();
        if (subjects.length == 1) return subjects;
        return Arrays.stream(subjects).filter(subject -> !subject.getVoided()).toArray(Subject[]::new);
    }

    public Subject getSubject(Date lastModifiedDateTime, String subjectType, HashMap<String, Object> concepts) {
        return pickAndExpectOne(getSubjects(lastModifiedDateTime, subjectType, concepts));
    }

    public Household getHousehold(String id) {
        ResponseEntity<Household> responseEntity = avniHttpClient.get(String.format("/api/groupSubjects?groupSubjectId=%s", id), Household.class);
        return responseEntity.getBody();
    }

    public Subject getSubject(String id) {
        ResponseEntity<Subject> responseEntity = avniHttpClient.get(String.format("/api/subject/%s", id), Subject.class);
        return responseEntity.getBody();
    }

    public Subject create(Subject subject) {
        ResponseEntity<Subject> responseEntity = avniHttpClient.post("/api/subject", subject, Subject.class);
        return responseEntity.getBody();
    }

    public Subject delete(String deletedEntity) {
        String json = null;
        HashMap<String, String> queryParams = new HashMap<>();
        ResponseEntity<Subject> responseEntity = avniHttpClient.delete(String.format("/api/subject/%s", deletedEntity), queryParams, json, Subject.class);
        return responseEntity.getBody();
    }
}
