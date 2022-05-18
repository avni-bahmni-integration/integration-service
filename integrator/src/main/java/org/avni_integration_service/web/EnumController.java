package org.avni_integration_service.web;

import org.avni_integration_service.integration_data.domain.BaseEnum;
import org.avni_integration_service.integration_data.domain.ErrorType;
import org.avni_integration_service.integration_data.domain.MappingGroup;
import org.avni_integration_service.integration_data.domain.MappingType;
import org.avni_integration_service.web.response.EnumResponse;
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
    public List<EnumResponse> getMappingGroups(Pageable pageable) {
        return getEnumResponses(MappingGroup.values());
    }

    private List<EnumResponse> getEnumResponses(BaseEnum[] values) {
        return Arrays.stream(values).map((EnumResponse::new)).sorted(Comparator.comparing(EnumResponse::getName)).collect(Collectors.toList());
    }

    @RequestMapping(value = "/mappingType", method = {RequestMethod.GET})
    public List<EnumResponse> getMappingTypes(Pageable pageable) {
        return getEnumResponses(MappingType.values());
    }

    @RequestMapping(value = "/errorType", method = {RequestMethod.GET})
    public List<EnumResponse> getErrorTypes(Pageable pageable) {
        return Arrays.stream(ErrorType.values()).map(errorType -> new EnumResponse(errorType.getValue(), errorType.name())).sorted(Comparator.comparing(EnumResponse::getName)).collect(Collectors.toList());
    }
}
