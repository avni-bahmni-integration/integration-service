package org.bahmni_avni_integration.repository.openmrs;

import org.bahmni_avni_integration.client.OpenMRSWebClient;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSEntity;
import org.bahmni_avni_integration.util.ObjectJsonMapper;
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