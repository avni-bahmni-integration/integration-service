package org.bahmni_avni_integration.web;

import org.bahmni_avni_integration.integration_data.domain.BaseEnum;
import org.bahmni_avni_integration.integration_data.domain.MappingGroup;
import org.bahmni_avni_integration.integration_data.domain.MappingType;
import org.bahmni_avni_integration.web.response.EnumResponse;
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
}
