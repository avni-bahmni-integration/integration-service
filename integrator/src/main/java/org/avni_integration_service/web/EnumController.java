package org.avni_integration_service.web;

import org.avni_integration_service.integration_data.domain.framework.BaseEnum;
import org.avni_integration_service.integration_data.domain.error.ErrorType;
import org.avni_integration_service.integration_data.domain.MappingGroup;
import org.avni_integration_service.integration_data.domain.MappingType;
import org.avni_integration_service.web.response.NamedEntityResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
public class EnumController {
    @RequestMapping(value = "/mappingGroup", method = {RequestMethod.GET})
    public List<NamedEntityResponse> getMappingGroups(Pageable pageable) {
        return getEnumResponses(MappingGroup.values());
    }

    private List<NamedEntityResponse> getEnumResponses(BaseEnum[] values) {
        return Arrays.stream(values).map((NamedEntityResponse::new)).sorted(Comparator.comparing(NamedEntityResponse::getName)).collect(Collectors.toList());
    }

    @RequestMapping(value = "/mappingType", method = {RequestMethod.GET})
    public List<NamedEntityResponse> getMappingTypes(Pageable pageable) {
        return getEnumResponses(MappingType.values());
    }

    @RequestMapping(value = "/errorType", method = {RequestMethod.GET})
    public List<NamedEntityResponse> getErrorTypes(Pageable pageable) {
        return Arrays.stream(ErrorType.values()).map(errorType -> new NamedEntityResponse(errorType.getValue(), errorType.name())).sorted(Comparator.comparing(NamedEntityResponse::getName)).collect(Collectors.toList());
    }
}
