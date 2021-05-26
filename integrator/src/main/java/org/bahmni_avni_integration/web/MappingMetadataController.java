package org.bahmni_avni_integration.web;

import org.bahmni_avni_integration.integration_data.domain.MappingMetaData;
import org.bahmni_avni_integration.integration_data.repository.MappingMetaDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/mappingMetadata")
public class MappingMetadataController {
    @Autowired
    private MappingMetaDataRepository mappingMetaDataRepository;

    @GetMapping(name = "/")
    @PreAuthorize("hasRole('USER')")
    public Page<MappingMetaData> getPage(Pageable pageable) {
        return mappingMetaDataRepository.findAll(pageable);
    }

    @RequestMapping(value = "/", method = {RequestMethod.POST, RequestMethod.PUT})
    @PreAuthorize("hasRole('USER')")
    public MappingMetaData save(MappingMetaData request) {
        MappingMetaData mappingMetaData;
        if (request.getId() == null || request.getId() == 0) {
            mappingMetaData = new MappingMetaData();
        } else {
            mappingMetaData = mappingMetaDataRepository.findById(request.getId()).get();
        }
        mappingMetaData.setMappingType(request.getMappingType());
        mappingMetaData.setMappingGroup(request.getMappingGroup());
        mappingMetaData.setBahmniValue(request.getBahmniValue());
        mappingMetaData.setAvniValue(request.getAvniValue());
        mappingMetaData.setDataTypeHint(request.getDataTypeHint());
        return mappingMetaDataRepository.save(mappingMetaData);
    }
}
