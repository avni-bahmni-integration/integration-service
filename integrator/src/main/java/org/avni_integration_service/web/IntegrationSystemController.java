package org.avni_integration_service.web;

import org.avni_integration_service.integration_data.repository.IntegrationSystemRepository;
import org.avni_integration_service.web.response.NamedEntityContract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class IntegrationSystemController {
    private final IntegrationSystemRepository integrationSystemRepository;

    @Autowired
    public IntegrationSystemController(IntegrationSystemRepository integrationSystemRepository) {
        this.integrationSystemRepository = integrationSystemRepository;
    }

    @RequestMapping(value = {"/integrationSystem"}, method = {RequestMethod.GET})
    public List<NamedEntityContract> getIntegrationSystems(Pageable pageable) {
        return integrationSystemRepository.findAll(pageable).stream().map((NamedEntityContract::new)).sorted(Comparator.comparing(NamedEntityContract::getName)).collect(Collectors.toList());
    }
}
