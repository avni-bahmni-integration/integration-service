package org.avni_integration_service.web;

import org.avni_integration_service.integration_data.domain.IntegrationSystem;
import org.avni_integration_service.integration_data.domain.MappingMetaData;
import org.avni_integration_service.integration_data.repository.MappingGroupRepository;
import org.avni_integration_service.integration_data.repository.MappingMetaDataRepository;
import org.avni_integration_service.integration_data.repository.MappingTypeRepository;
import org.avni_integration_service.integration_data.repository.UserRepository;
import org.avni_integration_service.util.ObsDataType;
import org.avni_integration_service.web.contract.MappingMetadataWebContract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.security.Principal;

@RestController
@PreAuthorize("hasRole('USER')")
public class MappingMetadataController extends BaseController {
    private final MappingMetaDataRepository mappingMetaDataRepository;
    private final MappingTypeRepository mappingTypeRepository;
    private final MappingGroupRepository mappingGroupRepository;

    @Autowired
    public MappingMetadataController(MappingMetaDataRepository mappingMetaDataRepository, UserRepository userRepository,
                                     MappingTypeRepository mappingTypeRepository, MappingGroupRepository mappingGroupRepository) {
        super(userRepository);
        this.mappingMetaDataRepository = mappingMetaDataRepository;
        this.mappingTypeRepository = mappingTypeRepository;
        this.mappingGroupRepository = mappingGroupRepository;
    }

    @RequestMapping(value = "/int/mappingMetadata", method = {RequestMethod.GET})
    public Page<MappingMetadataWebContract> getPage(Pageable pageable, Principal principal) {
        return toContractPage(mappingMetaDataRepository.findAllByIntegrationSystem(getCurrentIntegrationSystem(principal), pageable));
    }

    private Page<MappingMetadataWebContract> toContractPage(Page<MappingMetaData> page) {
        return page.map(this::mapOne);
    }

    @RequestMapping(value = "/int/mappingMetadata/{id}", method = {RequestMethod.GET})
    public MappingMetadataWebContract getOne(@PathVariable("id") int id, Principal principal) {
        IntegrationSystem currentIntegrationSystem = getCurrentIntegrationSystem(principal);
        MappingMetaData mappingMetaData = mappingMetaDataRepository.findByIdAndIntegrationSystem(id, currentIntegrationSystem);
        return mapOne(mappingMetaData);
    }

    private MappingMetadataWebContract mapOne(MappingMetaData mappingMetaData) {
        MappingMetadataWebContract mappingMetadataWebContract = new MappingMetadataWebContract();
        mappingMetadataWebContract.setMappingGroup(mappingMetaData.getMappingGroup().getId());
        mappingMetadataWebContract.setMappingType(mappingMetaData.getMappingType().getId());
        mappingMetadataWebContract.setIntSystemValue(mappingMetaData.getIntSystemValue());
        mappingMetadataWebContract.setAvniValue(mappingMetaData.getAvniValue());
        mappingMetadataWebContract.setId(mappingMetaData.getId());
        mappingMetadataWebContract.setCoded(mappingMetaData.isCoded());
        return mappingMetadataWebContract;
    }

    @RequestMapping(value = "/int/mappingMetadata/search/findByAvniValue", method = {RequestMethod.GET})
    public Page<MappingMetadataWebContract> findByAvniValue(@RequestParam("avniValue") String avniValue, Pageable pageable, Principal principal) {
        return toContractPage(mappingMetaDataRepository.findAllByAvniValueContainsAndIntegrationSystem(avniValue, getCurrentIntegrationSystem(principal), pageable));
    }

    @RequestMapping(value = "/int/mappingMetadata/search/findByIntSystemValue", method = {RequestMethod.GET})
    public Page<MappingMetadataWebContract> findByBahmniValue(@RequestParam("intSystemValue") String intSystemValue, Pageable pageable, Principal principal) {
        return toContractPage(mappingMetaDataRepository.findAllByIntSystemValueContainsAndIntegrationSystem(intSystemValue, getCurrentIntegrationSystem(principal), pageable));
    }

    @RequestMapping(value = "/int/mappingMetadata/search/find", method = {RequestMethod.GET})
    public Page<MappingMetadataWebContract> find(@RequestParam("avniValue") String avniValue,
                                                 @RequestParam("intSystemValue") String intSystemValue,
                                                 Pageable pageable, Principal principal) {
        return toContractPage(mappingMetaDataRepository.findAllByAvniValueContainsAndIntSystemValueContainsAndIntegrationSystem(avniValue, intSystemValue, getCurrentIntegrationSystem(principal), pageable));
    }

    @RequestMapping(value = "/int/mappingMetadata", method = {RequestMethod.POST})
    @Transactional
    public MappingMetadataWebContract create(@RequestBody MappingMetadataWebContract request, Principal principal) {
        IntegrationSystem iSystem =  getCurrentIntegrationSystem(principal);
        MappingMetaData mappingMetaData;
        if (request.getId() == 0) {
            mappingMetaData = new MappingMetaData();
        } else {
            mappingMetaData = mappingMetaDataRepository.findByIdAndIntegrationSystem(request.getId(), iSystem);
        }

        mappingMetaData.setMappingGroup(mappingGroupRepository.findById(request.getMappingGroup()).get());
        mappingMetaData.setMappingType(mappingTypeRepository.findById(request.getMappingType()).get());
        mappingMetaData.setIntSystemValue(request.getIntSystemValue());
        mappingMetaData.setAvniValue(request.getAvniValue());
        mappingMetaData.setDataTypeHint(request.isCoded() ? ObsDataType.Coded : null);
        mappingMetaData.setIntegrationSystem(iSystem);
        MappingMetaData saved = mappingMetaDataRepository.save(mappingMetaData);
        return mapOne(saved);
    }

    @RequestMapping(value = "/int/mappingMetadata/{id}", method = {RequestMethod.PUT})
    @Transactional
    public MappingMetadataWebContract update(@RequestBody MappingMetadataWebContract request, Principal principal) {
        return create(request, principal);
    }

    @RequestMapping(value = "/int/mappingMetadata/{id}", method = {RequestMethod.DELETE})
    @Transactional
    public void delete(@PathVariable("id") int id, Principal principal) {
        mappingMetaDataRepository.delete(mappingMetaDataRepository.findByIdAndIntegrationSystem(id, getCurrentIntegrationSystem(principal)));
    }
}
