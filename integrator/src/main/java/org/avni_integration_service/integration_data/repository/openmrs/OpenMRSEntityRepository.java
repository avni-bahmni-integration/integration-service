package org.avni_integration_service.integration_data.repository.openmrs;

import org.avni_integration_service.bahmni.client.OpenMRSWebClient;
import org.avni_integration_service.bahmni.contract.OpenMRSEntity;
import org.avni_integration_service.util.ObjectJsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpenMRSEntityRepository extends BaseOpenMRSRepository {

    @Autowired
    public OpenMRSEntityRepository(OpenMRSWebClient openMRSWebClient) {
        super(openMRSWebClient);
    }

    public OpenMRSEntity get(String resource, String uuid) {
        String json = openMRSWebClient.get(getSingleResourcePath(resource, uuid));
        return ObjectJsonMapper.readValue(json, OpenMRSEntity.class);
    }
}
