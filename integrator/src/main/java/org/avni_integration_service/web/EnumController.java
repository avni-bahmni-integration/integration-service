package org.avni_integration_service.web;

import org.avni_integration_service.integration_data.domain.MappingGroup;
import org.avni_integration_service.integration_data.domain.MappingType;
import org.avni_integration_service.integration_data.domain.error.ErrorType;
import org.avni_integration_service.integration_data.domain.framework.NamedIntegrationSpecificEntity;
import org.avni_integration_service.integration_data.repository.*;
import org.avni_integration_service.web.contract.NamedEntityContract;
import org.avni_integration_service.web.contract.NamedIntegrationSystemSpecificContract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@PreAuthorize("hasRole('USER')")
public class EnumController extends BaseController {
    private final MappingTypeRepository mappingTypeRepository;
    private final MappingGroupRepository mappingGroupRepository;
    private final ErrorTypeRepository errorTypeRepository;

    @Autowired
    public EnumController(MappingTypeRepository mappingTypeRepository, MappingGroupRepository mappingGroupRepository, UserRepository userRepository, ErrorTypeRepository errorTypeRepository) {
        super(userRepository);
        this.mappingTypeRepository = mappingTypeRepository;
        this.mappingGroupRepository = mappingGroupRepository;
        this.errorTypeRepository = errorTypeRepository;
    }

    private NamedIntegrationSystemSpecificContract getNamedEntityContract(BaseRepository baseRepository, int id) {
        NamedIntegrationSpecificEntity entity = (NamedIntegrationSpecificEntity) baseRepository.findEntity(id);
        return new NamedIntegrationSystemSpecificContract(entity);
    }

    private List<NamedIntegrationSystemSpecificContract> getEnumResponses(NamedIntegrationSpecificEntity[] values) {
        return Arrays.stream(values).map((NamedIntegrationSystemSpecificContract::new)).sorted(Comparator.comparing(NamedEntityContract::getName)).collect(Collectors.toList());
    }

    @RequestMapping(value = "/int/mappingGroup", method = {RequestMethod.GET})
    public List<NamedIntegrationSystemSpecificContract> getMappingGroups(Pageable pageable, Principal principal) {
        List<MappingGroup> mappingGroups = mappingGroupRepository.findAllByIntegrationSystem(getCurrentIntegrationSystem(principal));
        return getEnumResponses(mappingGroups.toArray(new MappingGroup[0]));
    }

    @RequestMapping(value = "/int/mappingGroup/{id}", method = {RequestMethod.GET})
    public NamedIntegrationSystemSpecificContract getMappingGroup(@PathVariable("id") int id) {
        return getNamedEntityContract(mappingGroupRepository, id);
    }

    @RequestMapping(value = "/int/mappingGroup", method = {RequestMethod.POST})
    @Transactional
    public NamedIntegrationSystemSpecificContract postMappingGroup(@RequestBody NamedEntityContract namedEntityContract) {
        MappingGroup mappingGroup = new MappingGroup(namedEntityContract.getName());
        return new NamedIntegrationSystemSpecificContract(mappingGroupRepository.save(mappingGroup));
    }

    @RequestMapping(value = "/int/mappingType", method = {RequestMethod.GET})
    public List<NamedIntegrationSystemSpecificContract> getMappingTypes(Pageable pageable, Principal principal) {
        List<MappingType> mappingTypes = mappingTypeRepository.findAllByIntegrationSystem(getCurrentIntegrationSystem(principal));
        return getEnumResponses(mappingTypes.toArray(new MappingType[0]));
    }

    @RequestMapping(value = "/int/mappingType/{id}", method = {RequestMethod.GET})
    public NamedIntegrationSystemSpecificContract getMappingType(@PathVariable("id") int id) {
        return getNamedEntityContract(mappingTypeRepository, id);
    }

    @RequestMapping(value = "/int/mappingType", method = {RequestMethod.POST})
    @Transactional
    public NamedIntegrationSystemSpecificContract postMappingType(@RequestBody NamedEntityContract namedEntityContract) {
        MappingType mappingType = new MappingType(namedEntityContract.getName());
        return new NamedIntegrationSystemSpecificContract(mappingTypeRepository.save(mappingType));
    }

    @RequestMapping(value = "/int/errorType", method = {RequestMethod.GET})
    public List<NamedEntityContract> getErrorTypes(Pageable pageable, Principal principal) {
        List<ErrorType> errorTypes = errorTypeRepository.findAllByIntegrationSystem(getCurrentIntegrationSystem(principal));
        return errorTypes.stream().map(errorType -> new NamedEntityContract(errorType.getValue(), errorType.getName())).sorted(Comparator.comparing(NamedEntityContract::getName)).collect(Collectors.toList());
    }
}
