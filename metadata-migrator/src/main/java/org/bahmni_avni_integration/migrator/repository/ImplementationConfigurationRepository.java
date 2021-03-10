package org.bahmni_avni_integration.migrator.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import org.bahmni_avni_integration.migrator.domain.OpenMRSForm;
import org.bahmni_avni_integration.migrator.util.FileUtil;
import org.bahmni_avni_integration.migrator.util.ObjectJsonMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ImplementationConfigurationRepository {
    public List<OpenMRSForm> getForms(String fileName) {
        return ObjectJsonMapper.readValue(FileUtil.readFile(fileName),  new TypeReference<List<OpenMRSForm>>(){});
    }
}