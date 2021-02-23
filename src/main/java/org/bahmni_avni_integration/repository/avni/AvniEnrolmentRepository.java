package org.bahmni_avni_integration.repository.avni;

import org.bahmni_avni_integration.client.AvniHttpClient;
import org.bahmni_avni_integration.contract.avni.Enrolment;
import org.bahmni_avni_integration.contract.avni.EnrolmentsResponse;
import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.contract.avni.SubjectsResponse;
import org.bahmni_avni_integration.util.FormatAndParseUtil;
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

    public Enrolment[] getEnrolments(Date lastModifiedDateTime) {
        Map<String, String> queryParams = Map.of("lastModifiedDateTime",
                FormatAndParseUtil.toISODateString(lastModifiedDateTime));
        ResponseEntity<EnrolmentsResponse> responseEntity = avniHttpClient.get("/api/enrolments", queryParams, EnrolmentsResponse.class);
        EnrolmentsResponse enrolmentsResponse = responseEntity.getBody();
        return enrolmentsResponse.getContent();
    }
}