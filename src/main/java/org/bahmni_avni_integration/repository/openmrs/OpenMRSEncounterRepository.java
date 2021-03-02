package org.bahmni_avni_integration.repository.openmrs;

import com.fasterxml.jackson.core.type.TypeReference;
import org.bahmni_avni_integration.client.OpenMRSWebClient;
import org.bahmni_avni_integration.contract.bahmni.*;
import org.bahmni_avni_integration.domain.MappingGroup;
import org.bahmni_avni_integration.domain.MappingType;
import org.bahmni_avni_integration.repository.MappingMetaDataRepository;
import org.bahmni_avni_integration.util.ObjectJsonMapper;
import org.ict4h.atomfeed.client.domain.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class OpenMRSEncounterRepository extends BaseOpenMRSRepository {
    private MappingMetaDataRepository mappingMetaDataRepository;

    @Autowired
    public OpenMRSEncounterRepository(OpenMRSWebClient openMRSWebClient, MappingMetaDataRepository mappingMetaDataRepository) {
        super(openMRSWebClient);
        this.mappingMetaDataRepository = mappingMetaDataRepository;
    }

    public OpenMRSEncounter getEncounterByUuid(String uuid) {
        String json = openMRSWebClient.get(getSingleResourcePath("encounter", uuid));
        return ObjectJsonMapper.readValue(json, OpenMRSEncounter.class);
    }

    public OpenMRSEncounter getRegistrationEncounterForAvniSubject(OpenMRSUuidHolder patient, String subjectId, String subjectUuidConceptUuid) {
        return getEncounterByPatientAndObservation(patient.getUuid(), subjectUuidConceptUuid, subjectId);
    }

    public OpenMRSEncounter getEncounterByPatientAndObservation(String patientUuid, String conceptUuid, String value) {
        String json = openMRSWebClient.get(URI.create(String.format("%s?patient=%s&obsConcept=%s&obsValues=%s", getResourcePath("encounter"), patientUuid, conceptUuid, encode(value))));
        SearchResults<OpenMRSEncounter> searchResults = ObjectJsonMapper.readValue(json, new TypeReference<SearchResults<OpenMRSEncounter>>() {
        });
        return pickAndExpectOne(searchResults, String.format("%s-%s-%s", patientUuid, conceptUuid, value));
    }

    public OpenMRSEncounter getRegistrationEncounterForAvniSubject(String subjectId, String subjectUuidConceptUuid) {
        String json = openMRSWebClient.get(URI.create(String.format("%s?obsConcept=%s&obsValues=%s", getResourcePath("encounter"), subjectUuidConceptUuid, encode(subjectId))));
        SearchResults<OpenMRSEncounter> searchResults = ObjectJsonMapper.readValue(json, new TypeReference<SearchResults<OpenMRSEncounter>>() {
        });
        return pickAndExpectOne(searchResults, String.format("%s-%s", subjectUuidConceptUuid, subjectId));
    }

    public OpenMRSEncounter getEncounter(String subjectId) {
        String conceptUuid = mappingMetaDataRepository.getBahmniValue(MappingGroup.PatientSubject, MappingType.SubjectUUID_Concept);
        String json = openMRSWebClient.get(URI.create(String.format("%s?obsConcept=%s&obsValues=%s", getResourcePath("encounter"), conceptUuid, encode(subjectId))));
        SearchResults<OpenMRSEncounter> searchResults = ObjectJsonMapper.readValue(json, new TypeReference<SearchResults<OpenMRSEncounter>>() {
        });
        return pickAndExpectOne(searchResults, String.format("%s-%s", conceptUuid, subjectId));
    }

    public OpenMRSFullEncounter createEncounter(OpenMRSEncounter encounter) {
        String json = ObjectJsonMapper.writeValueAsString(encounter);
        String outputJson = openMRSWebClient.post(getResourcePath("encounter"), json);
        return ObjectJsonMapper.readValue(outputJson, OpenMRSFullEncounter.class);
    }

    public OpenMRSFullEncounter updateEncounter(OpenMRSEncounter encounter) {
        String json = ObjectJsonMapper.writeValueAsString(encounter);
        String outputJson = openMRSWebClient.post(getResourcePath(String.format("encounter/%s", encounter.getUuid())), json);
        return ObjectJsonMapper.readValue(outputJson, OpenMRSFullEncounter.class);
    }

    public void deleteEncounter(OpenMRSBaseEncounter encounter) {
        openMRSWebClient.delete(URI.create(String.format("%s/%s?purge=true", getResourcePath("encounter"), encounter.getUuid())));
    }

    public OpenMRSFullEncounter getEncounter(Event event) {
        String json = getUnderlyingResourceJson(event);
        return ObjectJsonMapper.readValue(json, OpenMRSFullEncounter.class);
    }
}