package org.bahmni_avni_integration.web;

import org.bahmni_avni_integration.integration_data.domain.ErrorRecord;
import org.bahmni_avni_integration.integration_data.repository.ErrorRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/errorRecord")
public class ErrorRecordController {
    private final ErrorRecordRepository errorRecordRepository;

    @Autowired
    public ErrorRecordController(ErrorRecordRepository errorRecordRepository) {
        this.errorRecordRepository = errorRecordRepository;
    }

    @GetMapping(name = "/")
    @PreAuthorize("hasRole('USER')")
    public Page<ErrorRecord> getPage(Pageable pageable) {
        return errorRecordRepository.findAll(pageable);
    }
}
