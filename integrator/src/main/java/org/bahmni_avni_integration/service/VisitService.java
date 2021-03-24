package org.bahmni_avni_integration.service;

import org.apache.http.HttpStatus;
import org.bahmni_avni_integration.client.bahmni.WebClientsException;
import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.contract.bahmni.*;
import org.bahmni_avni_integration.integration_data.domain.ConstantKey;
import org.bahmni_avni_integration.integration_data.domain.Constants;
import org.bahmni_avni_integration.integration_data.domain.ErrorType;
import org.bahmni_avni_integration.integration_data.internal.SubjectToPatientMetaData;
import org.bahmni_avni_integration.integration_data.repository.ConstantsRepository;
import org.bahmni_avni_integration.integration_data.repository.openmrs.OpenMRSEncounterRepository;
import org.bahmni_avni_integration.integration_data.repository.openmrs.OpenMRSPatientRepository;
import org.bahmni_avni_integration.integration_data.repository.openmrs.OpenMRSVisitRepository;
import org.bahmni_avni_integration.mapper.avni.SubjectMapper;
import org.bahmni_avni_integration.repository.openmrs.OpenMRSPersonRepository;
import org.bahmni_avni_integration.util.FormatAndParseUtil;
import org.ict4h.atomfeed.client.domain.Event;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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