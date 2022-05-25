package org.avni_integration_service.web;

import org.avni_integration_service.integration_data.domain.IntegrationSystem;
import org.avni_integration_service.integration_data.repository.IntegrationSystemRepository;
import org.avni_integration_service.web.contract.NamedEntityContract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class IntegrationSystemController {
    private final IntegrationSystemRepository integrationSystemRepository;

    @Autowired
    public IntegrationSystemController(IntegrationSystemRepository integrationSystemRepository) {
        this.integrationSystemRepository = integrationSystemRepository;
    }

    @RequestMapping(value = {"/integrationSystem"}, method = {RequestMethod.GET})
    public List<NamedEntityContract> getIntegrationSystems(@RequestParam(value = "ids", required = false) String ids, Pageable pageable) {
        if (ids == null) {
            Page<IntegrationSystem> all = integrationSystemRepository.findAll(pageable);
            return mapStream(all.stream());
        } else {
            Integer[] ints = Arrays.stream(ids.split(",")).map(Integer::parseInt).toArray(Integer[]::new);
            return mapStream(integrationSystemRepository.findByIdIn(ints).stream());
        }
    }

    private List<NamedEntityContract> mapStream(Stream<IntegrationSystem> all) {
        return all.map((NamedEntityContract::new)).sorted(Comparator.comparing(NamedEntityContract::getName)).collect(Collectors.toList());
    }

    @RequestMapping(value = {"/integrationSystem/{id}"}, method = {RequestMethod.GET})
    public NamedEntityContract getIntegrationSystems(@PathVariable("id") int id) {
        IntegrationSystem integrationSystem = integrationSystemRepository.findById(id).get();
        return new NamedEntityContract(integrationSystem);
    }
}
