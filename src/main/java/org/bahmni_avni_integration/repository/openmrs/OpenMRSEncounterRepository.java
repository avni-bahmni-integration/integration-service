package org.bahmni_avni_integration.repository.openmrs;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.bahmni_avni_integration.client.OpenMRSWebClient;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSConcept;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSPatient;
import org.bahmni_avni_integration.contract.bahmni.SearchResults;
import org.bahmni_avni_integration.domain.Constants;
import org.bahmni_avni_integration.domain.MappingGroup;
import org.bahmni_avni_integration.domain.MappingType;
import org.bahmni_avni_integration.repository.MappingMetaDataRepository;
import org.bahmni_avni_integration.util.ObjectJsonMapper;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class OpenMRSEncounterRepository extends BaseOpenMRSRepository {
    @Autowired
    private OpenMRSPatientRepository openMRSPatientRepository;
    @Autowired
    private OpenMRSConceptRepository openMRSConceptRepository;
    @Autowired
    private OpenMRSWebClient openMRSWebClient;
    @Autowired
    private MappingMetaDataRepository mappingMetaDataRepository;

    public OpenMRSEncounter getEncounter(String uuid) {
        String json = openMRSWebClient.get(getSingleResourcePath("encounter", uuid));
        return ObjectJsonMapper.readValue(json, OpenMRSEncounter.class);
    }

    public Pair<OpenMRSPatient, OpenMRSEncounter> getEncounter(String patientIdentifier, String subjectId)  {
        String conceptUuid = mappingMetaDataRepository.getBahmniValue(MappingGroup.PatientSubject, MappingType.SubjectUUID_Concept);
        OpenMRSPatient patient = openMRSPatientRepository.getPatientByIdentifier(patientIdentifier);
        String json = openMRSWebClient.get(URI.create(String.format("%s?patient=%s&obsConcept=%s&obsValues=%s", getResourcePath("encounter"), patient.getUuid(), conceptUuid, encode(subjectId))));
        SearchResults<OpenMRSEncounter> searchResults = ObjectJsonMapper.readValue(json, new TypeReference<SearchResults<OpenMRSEncounter>>() {});
        OpenMRSEncounter encounter = pickAndExpectOne(searchResults, String.format("%s-%s-%s", patientIdentifier, conceptUuid, subjectId));
        return new Pair<OpenMRSPatient, OpenMRSEncounter>(patient, encounter);
    }

    public void createEncounter(OpenMRSEncounter encounter) {
        String json = ObjectJsonMapper.writeValueAsString(encounter);
        openMRSWebClient.post(getResourcePath("encounter"), json);
    }
}