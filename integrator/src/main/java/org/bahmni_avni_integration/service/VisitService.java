package org.bahmni_avni_integration.service;

import org.bahmni_avni_integration.contract.bahmni.*;
import org.bahmni_avni_integration.integration_data.domain.ConstantKey;
import org.bahmni_avni_integration.integration_data.repository.ConstantsRepository;
import org.bahmni_avni_integration.integration_data.repository.openmrs.OpenMRSVisitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class VisitService {
    private final ConstantsRepository constantsRepository;
    private final OpenMRSVisitRepository openMRSVisitRepository;
    private Logger logger = LoggerFactory.getLogger(getClass());

    public VisitService(ConstantsRepository constantsRepository, OpenMRSVisitRepository openMRSVisitRepository) {
        this.constantsRepository = constantsRepository;
        this.openMRSVisitRepository = openMRSVisitRepository;
    }

    public OpenMRSUuidHolder getVisit(String patientUuid) {
        String locationUuid = constantsRepository.findAllConstants().getValue(ConstantKey.IntegrationBahmniLocation);
        return openMRSVisitRepository.getVisit(patientUuid, locationUuid);
    }

    public OpenMRSUuidHolder createVisit(String patientUuid) {
        String location = constantsRepository.findAllConstants().getValue(ConstantKey.IntegrationBahmniLocation);
        String visitType = constantsRepository.findAllConstants().getValue(ConstantKey.IntegrationBahmniVisitType);
        OpenMRSSaveVisit openMRSSaveVisit = new OpenMRSSaveVisit();
        openMRSSaveVisit.setLocation(location);
        openMRSSaveVisit.setVisitType(visitType);
        openMRSSaveVisit.setPatient(patientUuid);
        OpenMRSUuidHolder visit = openMRSVisitRepository.createVisit(openMRSSaveVisit);
        logger.debug("Created new visit with uuid %s".formatted(visit.getUuid()));
        return visit;
    }

}