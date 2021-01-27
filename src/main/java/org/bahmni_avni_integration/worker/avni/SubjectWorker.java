package org.bahmni_avni_integration.worker.avni;

import org.bahmni_avni_integration.client.AvniHttpClient;
import org.bahmni_avni_integration.contract.avni.SubjectsResponse;
import org.bahmni_avni_integration.domain.*;
import org.bahmni_avni_integration.mapper.avni.SubjectMapper;
import org.bahmni_avni_integration.repository.AvniEntityStatusRepository;
import org.bahmni_avni_integration.repository.MappingMetaDataRepository;
import org.bahmni_avni_integration.repository.openmrs.OpenMRSPatientRepository;
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
    private OpenMRSPatientRepository openMRSPatientRepository;

    @Autowired
    public SubjectWorker(AvniHttpClient avniHttpClient, AvniEntityStatusRepository avniEntityStatusRepository,
                         MappingMetaDataRepository mappingMetaDataRepository, OpenMRSPatientRepository openMRSPatientRepository) {
        this.avniHttpClient = avniHttpClient;
        this.avniEntityStatusRepository = avniEntityStatusRepository;
        this.mappingMetaDataRepository = mappingMetaDataRepository;
        this.openMRSPatientRepository = openMRSPatientRepository;
    }

    public void processSubjects() {
        AvniEntityStatus status = avniEntityStatusRepository.findByEntityType(AvniEntityType.Subject);
        MappingMetaData patientSubjectMapping = mappingMetaDataRepository.findByMappingGroupAndMappingType(MappingGroup.PatientSubject, MappingType.PatientSubjectType);
        String readUpto = FormatUtil.toISODateString(status.getReadUpto());
        HashMap<String, String> queryParams = new HashMap<>(1);
        queryParams.put("lastModifiedDateTime", readUpto);
        queryParams.put("subjectType", patientSubjectMapping.getAvniValue());
        ResponseEntity<SubjectsResponse> subjectsResponse = avniHttpClient.get("/subjects", queryParams, SubjectsResponse.class);
        SubjectsResponse subjects = subjectsResponse.getBody();
        SubjectMapper subjectMapper = new SubjectMapper();
        Arrays.stream(subjects.getContent()).forEach(subjectResponse -> {
            String subjectUuid = (String) subjectResponse.get("ID");
            System.out.println(subjectResponse);
        });
    }
}