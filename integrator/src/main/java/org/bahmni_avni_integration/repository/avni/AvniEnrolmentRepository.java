package org.bahmni_avni_integration.repository.avni;

import org.bahmni_avni_integration.client.AvniHttpClient;
import org.bahmni_avni_integration.contract.avni.Enrolment;
import org.bahmni_avni_integration.contract.avni.EnrolmentsResponse;
import org.bahmni_avni_integration.util.FormatAndParseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class AvniEnrolmentRepository extends BaseAvniRepository {
    @Autowired
    private AvniHttpClient avniHttpClient;

    public Enrolment[] getEnrolments(Date lastModifiedDateTime) {
        Map<String, String> queryParams = Map.of("lastModifiedDateTime",
                FormatAndParseUtil.toISODateString(lastModifiedDateTime));
        ResponseEntity<EnrolmentsResponse> responseEntity = avniHttpClient.get("/api/enrolments", queryParams, EnrolmentsResponse.class);
        EnrolmentsResponse enrolmentsResponse = responseEntity.getBody();
        return enrolmentsResponse.getContent();
    }

    public Enrolment getEnrolment(String id) {
        ResponseEntity<Enrolment> responseEntity = avniHttpClient.get(String.format("/api/enrolment/%s", id), Enrolment.class);
        return responseEntity.getBody();
    }
}