package org.bahmni_avni_integration.repository.openmrs;

import com.fasterxml.jackson.core.type.TypeReference;
import org.bahmni_avni_integration.client.OpenMRSWebClient;
import org.bahmni_avni_integration.contract.bahmni.*;
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

    public OpenMRSEncounter getEncounterByUuid(String uuid) {
        String json = openMRSWebClient.get(getSingleResourcePath("encounter", uuid));
        return ObjectJsonMapper.readValue(json, OpenMRSEncounter.class);
    }

    public Pair<OpenMRSPatient, OpenMRSEncounter> getEncounter(String patientIdentifier, String subjectId, String subjectUuidConceptUuid)  {
        OpenMRSPatient patient = openMRSPatientRepository.getPatientByIdentifier(patientIdentifier);
        String json = openMRSWebClient.get(URI.create(String.format("%s?patient=%s&obsConcept=%s&obsValues=%s", getResourcePath("encounter"), patient.getUuid(), subjectUuidConceptUuid, encode(subjectId))));
        SearchResults<OpenMRSEncounter> searchResults = ObjectJsonMapper.readValue(json, new TypeReference<SearchResults<OpenMRSEncounter>>() {});
        OpenMRSEncounter encounter = pickAndExpectOne(searchResults, String.format("%s-%s-%s", patientIdentifier, subjectUuidConceptUuid, subjectId));
        return new Pair<>(patient, encounter);
    }

    public OpenMRSEncounter getEncounter(String subjectId) {
        String conceptUuid = mappingMetaDataRepository.getBahmniValue(MappingGroup.PatientSubject, MappingType.SubjectUUID_Concept);
        String json = openMRSWebClient.get(URI.create(String.format("%s?obsConcept=%s&obsValues=%s", getResourcePath("encounter"), conceptUuid, encode(subjectId))));
        SearchResults<OpenMRSEncounter> searchResults = ObjectJsonMapper.readValue(json, new TypeReference<SearchResults<OpenMRSEncounter>>() {});
        return pickAndExpectOne(searchResults, String.format("%s-%s", conceptUuid, subjectId));
    }

    public OpenMRSPostSaveEncounter createEncounter(OpenMRSEncounter encounter) {
        String json = ObjectJsonMapper.writeValueAsString(encounter);
        String outputJson = openMRSWebClient.post(getResourcePath("encounter"), json);
        return ObjectJsonMapper.readValue(outputJson, OpenMRSPostSaveEncounter.class);
    }

    public void updateEncounter(OpenMRSEncounter encounter) {
    }

    public void deleteEncounter(OpenMRSBaseEncounter encounter) {
        openMRSWebClient.delete(URI.create(String.format("%s/%s?purge=true", getResourcePath("encounter"), encounter.getUuid())));
    }
}