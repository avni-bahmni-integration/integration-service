package org.bahmni_avni_integration.repository.openmrs;

import org.bahmni_avni_integration.client.OpenMRSWebClient;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSSavePerson;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSUuidHolder;
import org.bahmni_avni_integration.util.ObjectJsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpenMRSPersonRepository extends BaseOpenMRSRepository {
    @Autowired
    public OpenMRSPersonRepository(OpenMRSWebClient openMRSWebClient) {
        super(openMRSWebClient);
    }

    public OpenMRSUuidHolder createPerson(OpenMRSSavePerson openMRSSavePerson) {
        String json = ObjectJsonMapper.writeValueAsString(openMRSSavePerson);
        String outputJson = openMRSWebClient.post(getResourcePath("person"), json);
        return ObjectJsonMapper.readValue(outputJson, OpenMRSUuidHolder.class);
    }
}