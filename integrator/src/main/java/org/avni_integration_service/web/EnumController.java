package org.avni_integration_service.web;

import org.avni_integration_service.integration_data.domain.framework.BaseEnum;
import org.avni_integration_service.integration_data.domain.error.ErrorType;
import org.avni_integration_service.integration_data.domain.MappingGroup;
import org.avni_integration_service.integration_data.domain.MappingType;
import org.avni_integration_service.integration_data.repository.MappingGroupRepository;
import org.avni_integration_service.integration_data.repository.MappingTypeRepository;
import org.avni_integration_service.integration_data.repository.UserRepository;
import org.avni_integration_service.web.contract.NamedEntityContract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
public class EnumController extends BaseController {
    private final MappingTypeRepository mappingTypeRepository;
    private final MappingGroupRepository mappingGroupRepository;

    @Autowired
    public EnumController(MappingTypeRepository mappingTypeRepository, MappingGroupRepository mappingGroupRepository, UserRepository userRepository) {
        super(userRepository);
        this.mappingTypeRepository = mappingTypeRepository;
        this.mappingGroupRepository = mappingGroupRepository;
    }

    @RequestMapping(value = "/mappingGroup", method = {RequestMethod.GET})
    public List<NamedEntityContract> getMappingGroups(Pageable pageable, Principal principal) {
        List<MappingGroup> mappingGroups = mappingGroupRepository.findAllByIntegrationSystem(getCurrentIntegrationSystem(principal));
        return getEnumResponses(mappingGroups.toArray(BaseEnum[]::new));
    }

    private List<NamedEntityContract> getEnumResponses(BaseEnum[] values) {
        return Arrays.stream(values).map((NamedEntityContract::new)).sorted(Comparator.comparing(NamedEntityContract::getName)).collect(Collectors.toList());
    }

    @RequestMapping(value = "/mappingType", method = {RequestMethod.GET})
    public List<NamedEntityContract> getMappingTypes(Pageable pageable, Principal principal) {
        List<MappingType> mappingTypes = mappingTypeRepository.findAllByIntegrationSystem(getCurrentIntegrationSystem(principal));
        return getEnumResponses(mappingTypes.toArray(BaseEnum[]::new));
    }

    @RequestMapping(value = "/errorType", method = {RequestMethod.GET})
    public List<NamedEntityContract> getErrorTypes(Pageable pageable) {
        return Arrays.stream(ErrorType.values()).map(errorType -> new NamedEntityContract(errorType.getValue(), errorType.name())).sorted(Comparator.comparing(NamedEntityContract::getName)).collect(Collectors.toList());
    }
}
