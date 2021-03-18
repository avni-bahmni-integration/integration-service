package org.bahmni_avni_integration.migrator.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import org.bahmni_avni_integration.migrator.domain.OpenMRSForm;
import org.bahmni_avni_integration.migrator.util.FileUtil;
import org.bahmni_avni_integration.migrator.util.ObjectJsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ImplementationConfigurationRepository {
    @Autowired
    private FileUtil fileUtil;

    public List<OpenMRSForm> getForms() {
        String bahmniFormFile = "bahmni/bahmni-forms.json";
        return ObjectJsonMapper.readValue(fileUtil.readConfigFile(bahmniFormFile),  new TypeReference<List<OpenMRSForm>>(){});
    }
}