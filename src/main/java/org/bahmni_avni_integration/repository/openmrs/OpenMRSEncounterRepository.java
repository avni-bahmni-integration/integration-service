package org.bahmni_avni_integration.repository.openmrs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.bahmni_avni_integration.client.OpenMRSWebClient;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSConcept;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSPatient;
import org.bahmni_avni_integration.contract.bahmni.SearchResults;
import org.bahmni_avni_integration.util.ObjectJsonMapper;
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

    public OpenMRSEncounter getEncounter(String uuid) {
        String json = openMRSWebClient.get(getSingleResourcePath("encounter", uuid));
        return ObjectJsonMapper.readValue(json, OpenMRSEncounter.class);
    }

    public OpenMRSEncounter getEncounter(String patientIdentifier, String conceptName, Object obsValue)  {
        OpenMRSPatient patient = openMRSPatientRepository.getPatientByIdentifier(patientIdentifier);
        OpenMRSConcept concept = openMRSConceptRepository.getConceptByName(conceptName);
        String json = openMRSWebClient.get(URI.create(String.format("%s?patient=%s&obsConcept=%s&obsValues=%s", getResourcePath("encounter"), patient.getUuid(), concept.getUuid(), encode(obsValue.toString()))));
        SearchResults<OpenMRSEncounter> searchResults = ObjectJsonMapper.readValue(json, new TypeReference<SearchResults<OpenMRSEncounter>>() {});
        return pickAndExpectOne(searchResults, String.format("%s-%s-%s", patientIdentifier, conceptName, obsValue));
    }

    public void createEncounter(OpenMRSEncounter encounter) {
        String json = ObjectJsonMapper.writeValueAsString(encounter);
        openMRSWebClient.post(getResourcePath("encounter"), json);
    }
}