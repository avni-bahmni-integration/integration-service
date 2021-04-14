package org.bahmni_avni_integration.integration_data.repository.openmrs;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.http.HttpStatus;
import org.bahmni_avni_integration.client.OpenMRSWebClient;
import org.bahmni_avni_integration.client.bahmni.WebClientsException;
import org.bahmni_avni_integration.contract.bahmni.*;
import org.bahmni_avni_integration.integration_data.domain.MappingGroup;
import org.bahmni_avni_integration.integration_data.domain.MappingType;
import org.bahmni_avni_integration.integration_data.repository.MappingMetaDataRepository;
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

    public OpenMRSFullEncounter getEncounterByUuid(String uuid) {
        String json = openMRSWebClient.get(String.format("%s?v=full", getSingleResourcePath("encounter", uuid)));
        return ObjectJsonMapper.readValue(json, OpenMRSFullEncounter.class);
    }

    public OpenMRSFullEncounter getEncounterByPatientAndObservation(String patientUuid, String conceptUuid, String value) {
        String json = openMRSWebClient.get(URI.create(String.format("%s?patient=%s&obsConcept=%s&obsValues=%s", getResourcePath("encounter"), patientUuid, conceptUuid, encode(value))));
        SearchResults<OpenMRSUuidHolder> searchResults = ObjectJsonMapper.readValue(json, new TypeReference<SearchResults<OpenMRSUuidHolder>>() {
        });
        OpenMRSUuidHolder encounterReference = pickAndExpectOne(searchResults, String.format("%s-%s-%s", patientUuid, conceptUuid, value));
        return encounterReference == null ? null : getEncounterByUuid(encounterReference.getUuid());
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