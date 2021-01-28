package org.bahmni_avni_integration.repository.openmrs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.bahmni_avni_integration.client.OpenMRSWebClient;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSConcept;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSPatient;
import org.bahmni_avni_integration.contract.bahmni.SearchResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;

import static org.bahmni.webclients.ObjectMapperRepository.objectMapper;

@Component
public class OpenMRSEncounterRepository extends BaseOpenMRSRepository {
    @Autowired
    private OpenMRSPatientRepository openMRSPatientRepository;

    @Autowired
    private OpenMRSConceptRepository openMRSConceptRepository;

    @Autowired
    private OpenMRSWebClient openMRSWebClient;

    public OpenMRSEncounter getEncounterByObservation(String patientIdentifier, String conceptName, Object obsValue) throws JsonProcessingException {
        OpenMRSPatient patient = openMRSPatientRepository.getPatientByIdentifier(patientIdentifier);
        OpenMRSConcept concept = openMRSConceptRepository.getConceptByName(conceptName);
        String patientJSON = openMRSWebClient.get(URI.create(String.format("%s?patient=%s&obsConcept=%s&obsValues=%s", getResourcePath("encounter"), patient.getUuid(), concept.getUuid(), encode(obsValue.toString()))));
        SearchResults<OpenMRSEncounter> searchResults = objectMapper.readValue(patientJSON, new TypeReference<SearchResults<OpenMRSEncounter>>() {});
        return pickAndExpectOne(searchResults, String.format("%s-%s-%s", patientIdentifier, conceptName, obsValue));
    }
}