package org.bahmni_avni_integration.web;

import org.bahmni_avni_integration.integration_data.domain.MappingGroup;
import org.bahmni_avni_integration.integration_data.domain.MappingMetaData;
import org.bahmni_avni_integration.integration_data.domain.MappingType;
import org.bahmni_avni_integration.integration_data.domain.ObsDataType;
import org.bahmni_avni_integration.integration_data.repository.MappingMetaDataRepository;
import org.bahmni_avni_integration.integration_data.util.EnumUtil;
import org.bahmni_avni_integration.web.request.MappingMetadataWebRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class MappingMetadataController {
    @Autowired
    private MappingMetaDataRepository mappingMetaDataRepository;

    @RequestMapping(value = "/mappingMetadata", method = {RequestMethod.GET})
    public Page<MappingMetaData> getPage(Pageable pageable) {
        return mappingMetaDataRepository.findAll(pageable);
    }

    @RequestMapping(value = "/mappingMetadata/{id}", method = {RequestMethod.GET})
    public MappingMetaData getOne(@PathVariable("id") int id) {
        return mappingMetaDataRepository.findById(id).get();
    }

    @RequestMapping(value = "/mappingMetadata/search/findByAvniValue", method = {RequestMethod.GET})
    public Page<MappingMetaData> findByAvniValue(@RequestParam("avniValue") String avniValue, Pageable pageable) {
        return mappingMetaDataRepository.findAllByAvniValueContains(avniValue, pageable);
    }

    @RequestMapping(value = "/mappingMetadata/search/findByBahmniValue")
    public Page<MappingMetaData> findByBahmniValue(@RequestParam("bahmniValue") String bahmniValue, Pageable pageable) {
        return mappingMetaDataRepository.findAllByBahmniValueContains(bahmniValue, pageable);
    }

    @RequestMapping(value = "/mappingMetadata/search/find", method = {RequestMethod.GET})
    public Page<MappingMetaData> find(@RequestParam("avniValue") String avniValue, @RequestParam("bahmniValue") String bahmniValue, Pageable pageable) {
        return mappingMetaDataRepository.findAllByAvniValueContainsAndBahmniValueContains(avniValue, bahmniValue, pageable);
    }

    @RequestMapping(value = "/mappingMetadata", method = {RequestMethod.POST, RequestMethod.PUT})
    @PreAuthorize("hasRole('USER')")
    public MappingMetaData save(@RequestBody MappingMetadataWebRequest request) {
        MappingMetaData mappingMetaData;
        if (request.getId() == 0) {
            mappingMetaData = new MappingMetaData();
        } else {
            mappingMetaData = mappingMetaDataRepository.findById(request.getId()).get();
        }
        mappingMetaData.setMappingGroup((MappingGroup) EnumUtil.findByValue(MappingGroup.values(), request.getMappingGroup()));
        mappingMetaData.setMappingType((MappingType) EnumUtil.findByValue(MappingType.values(), request.getMappingType()));
        mappingMetaData.setBahmniValue(request.getBahmniValue());
        mappingMetaData.setAvniValue(request.getAvniValue());
        mappingMetaData.setDataTypeHint(request.isCoded() ? ObsDataType.Coded : null);
        return mappingMetaDataRepository.save(mappingMetaData);
    }

    @RequestMapping(value = "/", method = {RequestMethod.DELETE})
    public void delete(int id) {
        mappingMetaDataRepository.delete(mappingMetaDataRepository.findById(id).get());
    }
}
