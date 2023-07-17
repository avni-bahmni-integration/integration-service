package org.avni_integration_service.web;

import org.avni_integration_service.integration_data.domain.IntegrationSystem;
import org.avni_integration_service.integration_data.domain.config.IntegrationSystemConfig;
import org.avni_integration_service.integration_data.repository.IntegrationSystemRepository;
import org.avni_integration_service.integration_data.repository.config.IntegrationSystemConfigRepository;
import org.avni_integration_service.web.contract.IntegrationSystemContract;
import org.avni_integration_service.web.contract.NamedEntityContract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@PreAuthorize("hasRole('USER')")
public class IntegrationSystemController {
    private final IntegrationSystemRepository integrationSystemRepository;

    @Autowired
    public IntegrationSystemController(IntegrationSystemRepository integrationSystemRepository) {
        this.integrationSystemRepository = integrationSystemRepository;
    }

    @RequestMapping(value = "/int/integrationSystem", method = {RequestMethod.GET})
    public List<IntegrationSystemContract> getIntegrationSystems(@RequestParam(value = "ids", required = false) String ids, Pageable pageable) {
        if (ids == null) {
            Page<IntegrationSystem> all = integrationSystemRepository.findAll(pageable);
            return mapStream(all.stream());
        } else {
            Integer[] ints = Arrays.stream(ids.split(",")).map(Integer::parseInt).toArray(Integer[]::new);
            return mapStream(integrationSystemRepository.findByIdIn(ints).stream());
        }
    }

    private List<IntegrationSystemContract> mapStream(Stream<IntegrationSystem> all) {
        return all.map(IntegrationSystemContract::new).sorted(Comparator.comparing(IntegrationSystemContract::getType)).collect(Collectors.toList());
    }

    @RequestMapping(value = "/int/integrationSystem/{id}", method = {RequestMethod.GET})
    public IntegrationSystemContract getIntegrationSystems(@PathVariable("id") int id) {
        IntegrationSystem integrationSystem = integrationSystemRepository.getEntity(id);
        return new IntegrationSystemContract(integrationSystem);
    }
}
