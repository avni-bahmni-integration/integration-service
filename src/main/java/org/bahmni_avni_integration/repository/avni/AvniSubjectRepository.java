package org.bahmni_avni_integration.repository.avni;

import org.bahmni_avni_integration.client.AvniHttpClient;
import org.bahmni_avni_integration.contract.avni.SubjectResponse;
import org.bahmni_avni_integration.contract.avni.SubjectsResponse;
import org.bahmni_avni_integration.util.FormatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;

@Component
public class AvniSubjectRepository {
    @Autowired
    private AvniHttpClient avniHttpClient;

    public SubjectResponse[] getSubjects(Date lastModifiedDateTime, String subjectType) {
        String fromTime = FormatUtil.toISODateString(lastModifiedDateTime);
        HashMap<String, String> queryParams = new HashMap<>(1);
        queryParams.put("lastModifiedDateTime", fromTime);
        queryParams.put("subjectType", subjectType);
        ResponseEntity<SubjectsResponse> subjectsResponse = avniHttpClient.get("/api/subjects", queryParams, SubjectsResponse.class);
        SubjectsResponse subjects = subjectsResponse.getBody();
        return subjects.getContent();
    }
}