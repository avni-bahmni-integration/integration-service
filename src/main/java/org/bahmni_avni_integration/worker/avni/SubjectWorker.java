package org.bahmni_avni_integration.worker.avni;

import org.bahmni_avni_integration.client.AvniHttpClient;
import org.bahmni_avni_integration.contract.avni.SubjectsResponse;
import org.bahmni_avni_integration.domain.*;
import org.bahmni_avni_integration.repository.AvniEntityStatusRepository;
import org.bahmni_avni_integration.repository.MappingMetaDataRepository;
import org.bahmni_avni_integration.util.FormatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;

@Component
public class SubjectWorker {
    private AvniHttpClient avniHttpClient;
    private AvniEntityStatusRepository avniEntityStatusRepository;
    private MappingMetaDataRepository mappingMetaDataRepository;

    @Autowired
    public SubjectWorker(AvniHttpClient avniHttpClient, AvniEntityStatusRepository avniEntityStatusRepository, MappingMetaDataRepository mappingMetaDataRepository) {
        this.avniHttpClient = avniHttpClient;
        this.avniEntityStatusRepository = avniEntityStatusRepository;
        this.mappingMetaDataRepository = mappingMetaDataRepository;
    }

    void processSubjects() {
        AvniEntityStatus status = avniEntityStatusRepository.findByEntityType(AvniEntityType.Subject);
        MappingMetaData patientSubjectMapping = mappingMetaDataRepository.findByMappingGroupAndMappingType(MappingGroup.PatientSubject, MappingType.PatientSubjectType);
        String readUpto = FormatUtil.toISODateString(status.getReadUpto());
        HashMap<String, String> queryParams = new HashMap<>(1);
        queryParams.put("lastModifiedDateTime", readUpto);
        queryParams.put("subjectType", patientSubjectMapping.getAvniValue());
        ResponseEntity<SubjectsResponse> subjectsResponse = avniHttpClient.get("/subjects", queryParams, SubjectsResponse.class);
        SubjectsResponse subjects = subjectsResponse.getBody();
        Arrays.stream(subjects.getContent()).forEach(subjectResponse -> {
            System.out.println(subjectResponse);
        });
    }
}