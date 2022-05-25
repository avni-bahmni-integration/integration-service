package org.avni_integration_service.web;

import org.avni_integration_service.integration_data.domain.IntegrationSystem;
import org.avni_integration_service.integration_data.domain.MappingGroup;
import org.avni_integration_service.integration_data.domain.MappingMetaData;
import org.avni_integration_service.integration_data.domain.MappingType;
import org.avni_integration_service.integration_data.repository.MappingMetaDataRepository;
import org.avni_integration_service.integration_data.repository.UserRepository;
import org.avni_integration_service.util.ObsDataType;
import org.avni_integration_service.web.contract.MappingMetadataWebContract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/")
@PreAuthorize("hasRole('USER')")
public class MappingMetadataController extends BaseController {
    private final MappingMetaDataRepository mappingMetaDataRepository;

    public MappingMetadataController(MappingMetaDataRepository mappingMetaDataRepository, UserRepository userRepository) {
        super(userRepository);
        this.mappingMetaDataRepository = mappingMetaDataRepository;
    }

    @RequestMapping(value = "/mappingMetadata", method = {RequestMethod.GET})
    public Page<MappingMetadataWebContract> getPage(Pageable pageable, Principal principal) {
        return toContractPage(mappingMetaDataRepository.findAllByIntegrationSystem(getCurrentIntegrationSystem(principal), pageable));
    }

    private Page<MappingMetadataWebContract> toContractPage(Page<MappingMetaData> page) {
        return page.map(this::mapOne);
    }

    @RequestMapping(value = "/mappingMetadata/{id}", method = {RequestMethod.GET})
    public MappingMetadataWebContract getOne(@PathVariable("id") int id, Principal principal) {
        IntegrationSystem currentIntegrationSystem = getCurrentIntegrationSystem(principal);
        MappingMetaData mappingMetaData = mappingMetaDataRepository.findByIdAndIntegrationSystem(id, currentIntegrationSystem);
        return mapOne(mappingMetaData);
    }

    private MappingMetadataWebContract mapOne(MappingMetaData mappingMetaData) {
        MappingMetadataWebContract mappingMetadataWebContract = new MappingMetadataWebContract();
        mappingMetadataWebContract.setMappingGroup(MappingGroup.valueOf(mappingMetaData.getMappingGroup()).getValue());
        mappingMetadataWebContract.setMappingType(MappingType.valueOf(mappingMetaData.getMappingType()).getValue());
        mappingMetadataWebContract.setIntSystemValue(mappingMetaData.getIntSystemValue());
        mappingMetadataWebContract.setAvniValue(mappingMetaData.getAvniValue());
        mappingMetadataWebContract.setId(mappingMetaData.getId());
        mappingMetadataWebContract.setCoded(mappingMetaData.isCoded());
        return mappingMetadataWebContract;
    }

    @RequestMapping(value = "/mappingMetadata/search/findByAvniValue", method = {RequestMethod.GET})
    public Page<MappingMetadataWebContract> findByAvniValue(@RequestParam("avniValue") String avniValue, Pageable pageable, Principal principal) {
        return toContractPage(mappingMetaDataRepository.findAllByAvniValueContainsAndIntegrationSystem(avniValue, getCurrentIntegrationSystem(principal), pageable));
    }

    @RequestMapping(value = "/mappingMetadata/search/findByBahmniValue")
    public Page<MappingMetadataWebContract> findByBahmniValue(@RequestParam("bahmniValue") String bahmniValue, Pageable pageable, Principal principal) {
        return toContractPage(mappingMetaDataRepository.findAllByIntSystemValueContainsAndIntegrationSystem(bahmniValue, getCurrentIntegrationSystem(principal), pageable));
    }

    @RequestMapping(value = "/mappingMetadata/search/find", method = {RequestMethod.GET})
    public Page<MappingMetadataWebContract> find(@RequestParam("avniValue") String avniValue, @RequestParam("bahmniValue") String bahmniValue, Pageable pageable, Principal principal) {
        return toContractPage(mappingMetaDataRepository.findAllByAvniValueContainsAndIntSystemValueContainsAndIntegrationSystem(avniValue, bahmniValue, getCurrentIntegrationSystem(principal), pageable));
    }

    @RequestMapping(value = "/mappingMetadata", method = {RequestMethod.POST})
    public MappingMetadataWebContract create(@RequestBody MappingMetadataWebContract request, Principal principal) {
        MappingMetaData mappingMetaData;
        if (request.getId() == 0) {
            mappingMetaData = new MappingMetaData();
        } else {
            mappingMetaData = mappingMetaDataRepository.findByIdAndIntegrationSystem(request.getId(), getCurrentIntegrationSystem(principal));
        }
        mappingMetaData.setMappingGroup(MappingGroup.valueOf(request.getMappingGroup()).name());
        mappingMetaData.setMappingType(MappingType.valueOf(request.getMappingType()).name());
        mappingMetaData.setIntSystemValue(request.getIntSystemValue());
        mappingMetaData.setAvniValue(request.getAvniValue());
        mappingMetaData.setDataTypeHint(request.isCoded() ? ObsDataType.Coded : null);
        MappingMetaData saved = mappingMetaDataRepository.save(mappingMetaData);
        return mapOne(saved);
    }

    @RequestMapping(value = "/mappingMetadata/{id}", method = {RequestMethod.PUT})
    @PreAuthorize("hasRole('USER')")
    public MappingMetadataWebContract update(@RequestBody MappingMetadataWebContract request, Principal principal) {
        return create(request, principal);
    }

    @RequestMapping(value = "/mappingMetadata/{id}", method = {RequestMethod.DELETE})
    public void delete(@PathVariable("id") int id, Principal principal) {
        mappingMetaDataRepository.delete(mappingMetaDataRepository.findByIdAndIntegrationSystem(id, getCurrentIntegrationSystem(principal)));
    }
}
