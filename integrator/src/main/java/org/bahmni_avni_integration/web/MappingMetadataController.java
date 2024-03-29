package org.bahmni_avni_integration.web;

import org.bahmni_avni_integration.integration_data.domain.MappingGroup;
import org.bahmni_avni_integration.integration_data.domain.MappingMetaData;
import org.bahmni_avni_integration.integration_data.domain.MappingType;
import org.bahmni_avni_integration.integration_data.domain.ObsDataType;
import org.bahmni_avni_integration.integration_data.repository.MappingMetaDataRepository;
import org.bahmni_avni_integration.integration_data.util.EnumUtil;
import org.bahmni_avni_integration.web.contract.MappingMetadataWebContract;
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
    public Page<MappingMetadataWebContract> getPage(Pageable pageable) {
        return toContractPage(mappingMetaDataRepository.findAll(pageable));
    }

    private Page<MappingMetadataWebContract> toContractPage(Page<MappingMetaData> page) {
        return page.map(mappingMetaData -> getOne(mappingMetaData.getId()));
    }

    @RequestMapping(value = "/mappingMetadata/{id}", method = {RequestMethod.GET})
    public MappingMetadataWebContract getOne(@PathVariable("id") int id) {
        MappingMetaData mappingMetaData = mappingMetaDataRepository.findById(id).get();
        MappingMetadataWebContract mappingMetadataWebContract = new MappingMetadataWebContract();
        mappingMetadataWebContract.setMappingGroup(mappingMetaData.getMappingGroup().getValue());
        mappingMetadataWebContract.setMappingType(mappingMetaData.getMappingType().getValue());
        mappingMetadataWebContract.setBahmniValue(mappingMetaData.getBahmniValue());
        mappingMetadataWebContract.setAvniValue(mappingMetaData.getAvniValue());
        mappingMetadataWebContract.setId(mappingMetaData.getId());
        mappingMetadataWebContract.setCoded(mappingMetaData.isCoded());
        return mappingMetadataWebContract;
    }

    @RequestMapping(value = "/mappingMetadata/search/findByAvniValue", method = {RequestMethod.GET})
    public Page<MappingMetadataWebContract> findByAvniValue(@RequestParam("avniValue") String avniValue, Pageable pageable) {
        return toContractPage(mappingMetaDataRepository.findAllByAvniValueContains(avniValue, pageable));
    }

    @RequestMapping(value = "/mappingMetadata/search/findByBahmniValue")
    public Page<MappingMetadataWebContract> findByBahmniValue(@RequestParam("bahmniValue") String bahmniValue, Pageable pageable) {
        return toContractPage(mappingMetaDataRepository.findAllByBahmniValueContains(bahmniValue, pageable));
    }

    @RequestMapping(value = "/mappingMetadata/search/find", method = {RequestMethod.GET})
    public Page<MappingMetadataWebContract> find(@RequestParam("avniValue") String avniValue, @RequestParam("bahmniValue") String bahmniValue, Pageable pageable) {
        return toContractPage(mappingMetaDataRepository.findAllByAvniValueContainsAndBahmniValueContains(avniValue, bahmniValue, pageable));
    }

    @RequestMapping(value = "/mappingMetadata", method = {RequestMethod.POST})
    @PreAuthorize("hasRole('USER')")
    public MappingMetadataWebContract create(@RequestBody MappingMetadataWebContract request) {
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
        MappingMetaData saved = mappingMetaDataRepository.save(mappingMetaData);
        return getOne(saved.getId());
    }

    @RequestMapping(value = "/mappingMetadata/{id}", method = {RequestMethod.PUT})
    @PreAuthorize("hasRole('USER')")
    public MappingMetadataWebContract update(@RequestBody MappingMetadataWebContract request) {
        return create(request);
    }

    @RequestMapping(value = "/mappingMetadata/{id}", method = {RequestMethod.DELETE})
    public void delete(@PathVariable("id") int id) {
        mappingMetaDataRepository.delete(mappingMetaDataRepository.findById(id).get());
    }
}
