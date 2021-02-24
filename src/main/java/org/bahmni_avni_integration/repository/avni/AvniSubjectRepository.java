package org.bahmni_avni_integration.repository.avni;

import org.bahmni_avni_integration.client.AvniHttpClient;
import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.contract.avni.SubjectsResponse;
import org.bahmni_avni_integration.util.FormatAndParseUtil;
import org.bahmni_avni_integration.util.ObjectJsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;

@Component
public class AvniSubjectRepository extends BaseAvniRepository {
    @Autowired
    private AvniHttpClient avniHttpClient;

    public Subject[] getSubjects(Date lastModifiedDateTime, String subjectType) {
        String fromTime = FormatAndParseUtil.toISODateString(lastModifiedDateTime);
        HashMap<String, String> queryParams = new HashMap<>(1);
        queryParams.put("lastModifiedDateTime", fromTime);
        queryParams.put("subjectType", subjectType);
        ResponseEntity<SubjectsResponse> subjectsResponse = avniHttpClient.get("/api/subjects", queryParams, SubjectsResponse.class);
        SubjectsResponse subjects = subjectsResponse.getBody();
        return subjects.getContent();
    }

    public Subject[] getSubjects(Date lastModifiedDateTime, String subjectType, HashMap<String, Object> concepts) {
        String fromTime = FormatAndParseUtil.toISODateString(lastModifiedDateTime);
        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("lastModifiedDateTime", fromTime);
        queryParams.put("subjectType", subjectType);
        queryParams.put("concepts", ObjectJsonMapper.writeValueAsString(concepts));
        ResponseEntity<SubjectsResponse> responseEntity = avniHttpClient.get("/api/subjects", queryParams, SubjectsResponse.class);
        return responseEntity.getBody().getContent();
    }

    public Subject getSubject(Date lastModifiedDateTime, String subjectType, HashMap<String, Object> concepts) {
        return pickAndExpectOne(getSubjects(lastModifiedDateTime, subjectType, concepts));
    }

    public Subject getSubject(String id) {
        ResponseEntity<Subject> responseEntity = avniHttpClient.get(String.format("/api/subject/%s", id), Subject.class);
        return responseEntity.getBody();
    }

}