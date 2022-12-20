package org.avni_integration_service.avni.repository;

import org.avni_integration_service.avni.client.AvniHttpClient;
import org.avni_integration_service.avni.domain.Enrolment;
import org.avni_integration_service.avni.domain.EnrolmentsResponse;
import org.avni_integration_service.util.FormatAndParseUtil;
import org.avni_integration_service.util.ObjectJsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class AvniEnrolmentRepository extends BaseAvniRepository {
    @Autowired
    private AvniHttpClient avniHttpClient;

    public EnrolmentsResponse getEnrolments(Date lastModifiedDateTime) {
        Map<String, String> queryParams = Map.of("lastModifiedDateTime",
                FormatAndParseUtil.toISODateTimeString(lastModifiedDateTime),
                "size", "10");
        ResponseEntity<EnrolmentsResponse> responseEntity = avniHttpClient.get("/api/programEnrolments", queryParams, EnrolmentsResponse.class);
        return responseEntity.getBody();
    }

    public EnrolmentsResponse getEnrolments(Date lastModifiedDateTime, String program) {
        Map<String, String> queryParams = Map.of("lastModifiedDateTime",
                FormatAndParseUtil.toISODateTimeString(lastModifiedDateTime),
                "program", program,
                "size", "10");
        ResponseEntity<EnrolmentsResponse> responseEntity = avniHttpClient.get("/api/programEnrolments", queryParams, EnrolmentsResponse.class);
        return responseEntity.getBody();
    }

    public Enrolment getEnrolment(String id) {
        ResponseEntity<Enrolment> responseEntity = avniHttpClient.get(String.format("/api/programEnrolment/%s", id), Enrolment.class);
        return responseEntity.getBody();
    }

    public Enrolment getEnrolment(String subjectId, String program, Map<String, Object> obsCriteria) {
        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("concepts", ObjectJsonMapper.writeValueAsString(obsCriteria));
        queryParams.put("program", program);
        queryParams.put("subject", subjectId);
        ResponseEntity<EnrolmentsResponse> responseEntity = avniHttpClient.get("/api/programEnrolments", queryParams, EnrolmentsResponse.class);
        return pickAndExpectOne(responseEntity.getBody().getContent());
    }

    public Enrolment update(String id, Enrolment enrolment) {
        ResponseEntity<Enrolment> responseEntity = avniHttpClient.put(String.format("/api/programEnrolment/%s", id), enrolment, Enrolment.class);
        return responseEntity.getBody();
    }

    public Enrolment create(Enrolment enrolment) {
        ResponseEntity<Enrolment> responseEntity = avniHttpClient.post("/api/programEnrolment", enrolment, Enrolment.class);
        return responseEntity.getBody();
    }

    public Enrolment[] getEnrolments(String subjectExternalId, String program) {
        Map<String, String> queryParams = Map.of("subject", subjectExternalId, "program", program);
        ResponseEntity<EnrolmentsResponse> responseEntity = avniHttpClient.get("/api/programEnrolments", queryParams, EnrolmentsResponse.class);
        return responseEntity.getBody().getContent();
    }

    public Enrolment delete(String deletedEntity) {
        String json = null;
        HashMap<String, String> queryParams = new HashMap<>();
        ResponseEntity<Enrolment> responseEntity = avniHttpClient.delete(String.format("/api/programEnrolment/%s", deletedEntity), queryParams, json, Enrolment.class);
        return responseEntity.getBody();
    }
}
