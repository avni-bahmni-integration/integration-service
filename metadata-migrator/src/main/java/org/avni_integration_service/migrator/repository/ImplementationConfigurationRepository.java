package org.avni_integration_service.migrator.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import org.avni_integration_service.migrator.domain.OpenMRSForm;
import org.avni_integration_service.migrator.domain.StandardMappings;
import org.avni_integration_service.integration_data.util.FileUtil;
import org.avni_integration_service.migrator.util.ObjectJsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ImplementationConfigurationRepository {
    @Autowired
    private FileUtil fileUtil;

    public List<OpenMRSForm> getForms() {
        return ObjectJsonMapper.readValue(fileUtil.readConfigFile("bahmni/bahmni-forms.json"),  new TypeReference<List<OpenMRSForm>>(){});
    }

    public Map<String, Object> getConstants() {
        return ObjectJsonMapper.readValue(fileUtil.readConfigFile("integration/constants.json"),  new TypeReference<Map<String, Object>>(){});
    }

    public StandardMappings getStandardMappings() {
        return new StandardMappings(ObjectJsonMapper.readValue(fileUtil.readConfigFile("integration/standard-mappings.json"),  new TypeReference<List<Map<String, String>>>(){}));
    }

    public List<String> getIgnoredConcepts() {
        return ObjectJsonMapper.readValue(fileUtil.readConfigFile("bahmni/ignored-concepts.json"),  new TypeReference<List<String>>(){});
    }
}
