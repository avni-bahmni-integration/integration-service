package org.bahmni_avni_integration.integration_data.repository.avni;

import org.bahmni_avni_integration.client.AvniHttpClient;
import org.bahmni_avni_integration.contract.avni.Enrolment;
import org.bahmni_avni_integration.contract.avni.EnrolmentsResponse;
import org.bahmni_avni_integration.integration_data.util.FormatAndParseUtil;
import org.bahmni_avni_integration.util.ObjectJsonMapper;
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
                "size", "100");
        ResponseEntity<EnrolmentsResponse> responseEntity = avniHttpClient.get("/api/programEnrolments", queryParams, EnrolmentsResponse.class);
        return responseEntity.getBody();
    }

    public Enrolment getEnrolment(String id) {
        ResponseEntity<Enrolment> responseEntity = avniHttpClient.get(String.format("/api/programEnrolment/%s", id), Enrolment.class);
        return responseEntity.getBody();
    }

    public Enrolment getEnrolment(String program, Map<String, Object> obsCriteria) {
        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("concepts", ObjectJsonMapper.writeValueAsString(obsCriteria));
        queryParams.put("program", program);
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
}
