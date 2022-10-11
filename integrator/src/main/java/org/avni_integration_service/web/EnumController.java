package org.avni_integration_service.web;

import org.avni_integration_service.integration_data.domain.MappingGroup;
import org.avni_integration_service.integration_data.domain.MappingType;
import org.avni_integration_service.integration_data.domain.error.ErrorType;
import org.avni_integration_service.integration_data.domain.framework.NamedIntegrationSpecificEntity;
import org.avni_integration_service.integration_data.repository.*;
import org.avni_integration_service.web.contract.ErrorTypeContract;
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
import java.util.stream.Stream;

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

    @RequestMapping(value = "/int/mappingGroup", method = {RequestMethod.GET})
    public List<NamedIntegrationSystemSpecificContract> getMappingGroups(@RequestParam(value = "ids", required = false) String ids,
                                                                         Pageable pageable, Principal principal) {
       if (ids == null) {
            List<MappingGroup> mappingGroups = mappingGroupRepository.findAllByIntegrationSystem(getCurrentIntegrationSystem(principal));
            return mapStream(mappingGroups.stream().map(ent -> ent));
        } else {
            Integer[] eTypes = Arrays.stream(ids.split(",")).map(Integer::parseInt).toArray(Integer[]::new);
            return mapStream(mappingGroupRepository.findByIdIn(eTypes).stream().map(ent -> ent));
        }
    }

    @RequestMapping(value = "/int/mappingGroup/{id}", method = {RequestMethod.GET})
    public NamedIntegrationSystemSpecificContract getMappingGroup(@PathVariable("id") int id) {
        return getNamedEntityContract(mappingGroupRepository, id);
    }

    @RequestMapping(value = "/int/mappingGroup", method = {RequestMethod.POST})
    @Transactional
    public NamedIntegrationSystemSpecificContract postMappingGroup(@RequestBody NamedEntityContract namedEntityContract, Principal principal) {
        MappingGroup mappingGroup = new MappingGroup(namedEntityContract.getName(), getCurrentIntegrationSystem(principal));
        return new NamedIntegrationSystemSpecificContract(mappingGroupRepository.save(mappingGroup));
    }

    @RequestMapping(value = "/int/mappingType", method = {RequestMethod.GET})
    public List<NamedIntegrationSystemSpecificContract> getMappingTypes(@RequestParam(value = "ids", required = false) String ids,
                                                                        Pageable pageable, Principal principal) {
      if (ids == null) {
            List<MappingType> mappingTypes = mappingTypeRepository.findAllByIntegrationSystem(getCurrentIntegrationSystem(principal));
            return mapStream(mappingTypes.stream().map(ent -> ent));
        } else {
            Integer[] eTypes = Arrays.stream(ids.split(",")).map(Integer::parseInt).toArray(Integer[]::new);
            return mapStream(mappingTypeRepository.findByIdIn(eTypes).stream().map(ent -> ent));
        }
    }

    @RequestMapping(value = "/int/mappingType/{id}", method = {RequestMethod.GET})
    public NamedIntegrationSystemSpecificContract getMappingType(@PathVariable("id") int id) {
        return getNamedEntityContract(mappingTypeRepository, id);
    }

    @RequestMapping(value = "/int/mappingType", method = {RequestMethod.POST})
    @Transactional
    public NamedIntegrationSystemSpecificContract postMappingType(@RequestBody NamedEntityContract namedEntityContract, Principal principal) {
        MappingType mappingType = new MappingType(namedEntityContract.getName(), getCurrentIntegrationSystem(principal));
        return new NamedIntegrationSystemSpecificContract(mappingTypeRepository.save(mappingType));
    }

    @RequestMapping(value = "/int/errorType", method = {RequestMethod.GET})
    public List<ErrorTypeContract> getErrorTypes(@RequestParam(value = "ids", required = false) String ids,
                                                                      Pageable pageable, Principal principal) {
        if (ids == null) {
            List<ErrorType> all = errorTypeRepository.findAllByIntegrationSystem(getCurrentIntegrationSystem(principal));
            return mapErrorTypeStream(all.stream().map(ent -> ent));
        } else {
            Integer[] eTypes = Arrays.stream(ids.split(",")).map(Integer::parseInt).toArray(Integer[]::new);
            return mapErrorTypeStream(errorTypeRepository.findByIdIn(eTypes).stream().map(ent -> ent));
        }
    }

    @RequestMapping(value = "/int/errorType/{id}", method = {RequestMethod.GET})
    public ErrorTypeContract getErrorType(@PathVariable("id") int id) {
        return getErrorTypeContract(errorTypeRepository, id);
    }

    @RequestMapping(value = "/int/errorType", method = {RequestMethod.POST})
    @Transactional
    public ErrorTypeContract postErrorType(@RequestBody ErrorTypeContract errorTypeContract, Principal principal) {
        ErrorType errorType = new ErrorType(errorTypeContract.getName(), getCurrentIntegrationSystem(principal),
                errorTypeContract.getComparisonOperator(), errorTypeContract.getComparisonValue());
        return new ErrorTypeContract(errorTypeRepository.save(errorType));
    }

    private NamedIntegrationSystemSpecificContract getNamedEntityContract(BaseRepository baseRepository, int id) {
        NamedIntegrationSpecificEntity entity = (NamedIntegrationSpecificEntity) baseRepository.findEntity(id);
        return new NamedIntegrationSystemSpecificContract(entity);
    }

    private List<NamedIntegrationSystemSpecificContract> getEnumResponses(NamedIntegrationSpecificEntity[] values) {
        return Arrays.stream(values).map((NamedIntegrationSystemSpecificContract::new)).sorted(Comparator.comparing(NamedEntityContract::getName)).collect(Collectors.toList());
    }

    private List<NamedIntegrationSystemSpecificContract> mapStream(Stream<NamedIntegrationSpecificEntity> all) {
        return all.map((NamedIntegrationSystemSpecificContract::new))
                .sorted(Comparator.comparing(NamedIntegrationSystemSpecificContract::getName))
                .collect(Collectors.toList());
    }

    private ErrorTypeContract getErrorTypeContract(BaseRepository baseRepository, int id) {
        ErrorType entity = (ErrorType) baseRepository.findEntity(id);
        return new ErrorTypeContract(entity);
    }

    private List<ErrorTypeContract> getErrorTypeEnumResponses(ErrorType[] values) {
        return Arrays.stream(values).map((ErrorTypeContract::new)).sorted(Comparator.comparing(ErrorTypeContract::getName)).collect(Collectors.toList());
    }

    private List<ErrorTypeContract> mapErrorTypeStream(Stream<ErrorType> all) {
        return all.map((ErrorTypeContract::new))
                .sorted(Comparator.comparing(ErrorTypeContract::getName))
                .collect(Collectors.toList());
    }
}
