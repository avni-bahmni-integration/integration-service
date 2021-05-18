package org.bahmni_avni_integration.service;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.contract.bahmni.*;
import org.bahmni_avni_integration.integration_data.domain.ConstantKey;
import org.bahmni_avni_integration.integration_data.repository.ConstantsRepository;
import org.bahmni_avni_integration.integration_data.repository.openmrs.OpenMRSVisitRepository;
import org.springframework.stereotype.Service;

@Service
public class VisitService {
    private final ConstantsRepository constantsRepository;
    private final OpenMRSVisitRepository openMRSVisitRepository;
    private static final Logger logger = Logger.getLogger(VisitService.class);

    public VisitService(ConstantsRepository constantsRepository, OpenMRSVisitRepository openMRSVisitRepository) {
        this.constantsRepository = constantsRepository;
        this.openMRSVisitRepository = openMRSVisitRepository;
    }

    private OpenMRSVisit getVisit(String patientUuid) {
        String locationUuid = constantsRepository.findAllConstants().getValue(ConstantKey.IntegrationBahmniLocation);
        return openMRSVisitRepository.getVisit(patientUuid, locationUuid);
    }

    private OpenMRSVisit createVisit(OpenMRSPatient patient) {
        String location = constantsRepository.findAllConstants().getValue(ConstantKey.IntegrationBahmniLocation);
        String visitType = constantsRepository.findAllConstants().getValue(ConstantKey.IntegrationBahmniVisitType);
        OpenMRSSaveVisit openMRSSaveVisit = new OpenMRSSaveVisit();
        openMRSSaveVisit.setLocation(location);
        openMRSSaveVisit.setVisitType(visitType);
        openMRSSaveVisit.setPatient(patient.getUuid());
        openMRSSaveVisit.setStartDatetime(patient.getAuditInfo().getDateCreated());
        OpenMRSVisit visit = openMRSVisitRepository.createVisit(openMRSSaveVisit);
        logger.debug("Created new visit with uuid %s".formatted(visit.getUuid()));
        return visit;
    }

    public OpenMRSVisit getOrCreateVisit(OpenMRSPatient patient) {
        var visit = getVisit(patient.getUuid());
        if (visit == null) {
            return createVisit(patient);
        }
        logger.debug("Retrieved existing visit with uuid %s".formatted(visit.getUuid()));
        return visit;
    }

}