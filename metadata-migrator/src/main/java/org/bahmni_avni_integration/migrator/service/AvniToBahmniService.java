package org.bahmni_avni_integration.migrator.service;

import org.bahmni_avni_integration.migrator.domain.AvniForm;
import org.bahmni_avni_integration.migrator.repository.AvniRepository;
import org.bahmni_avni_integration.migrator.repository.OpenMRSRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class AvniToBahmniService {
    private final OpenMRSRepository openMRSRepository;
    private final AvniRepository avniRepository;

    public AvniToBahmniService(OpenMRSRepository openMRSRepository, AvniRepository avniRepository) {
        this.openMRSRepository = openMRSRepository;
        this.avniRepository = avniRepository;
    }

    public void migrateForms() throws SQLException {
        List<AvniForm> forms = avniRepository.getForms();
        openMRSRepository.createOpenMRSForms(forms);
    }
}