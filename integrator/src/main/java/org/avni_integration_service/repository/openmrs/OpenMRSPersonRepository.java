package org.avni_integration_service.repository.openmrs;

import org.avni_integration_service.bahmni.client.OpenMRSWebClient;
import org.avni_integration_service.bahmni.contract.OpenMRSSavePerson;
import org.avni_integration_service.bahmni.contract.OpenMRSUuidHolder;
import org.avni_integration_service.integration_data.repository.openmrs.BaseOpenMRSRepository;
import org.avni_integration_service.util.ObjectJsonMapper;
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
