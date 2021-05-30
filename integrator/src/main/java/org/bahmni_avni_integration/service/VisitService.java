package org.bahmni_avni_integration.service;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.contract.avni.Enrolment;
import org.bahmni_avni_integration.contract.bahmni.*;
import org.bahmni_avni_integration.integration_data.domain.ConstantKey;
import org.bahmni_avni_integration.integration_data.domain.MappingGroup;
import org.bahmni_avni_integration.integration_data.domain.MappingMetaDataCollection;
import org.bahmni_avni_integration.integration_data.domain.MappingType;
import org.bahmni_avni_integration.integration_data.repository.ConstantsRepository;
import org.bahmni_avni_integration.integration_data.repository.MappingMetaDataRepository;
import org.bahmni_avni_integration.integration_data.repository.openmrs.OpenMRSVisitRepository;
import org.bahmni_avni_integration.integration_data.util.FormatAndParseUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VisitService {
    private final ConstantsRepository constantsRepository;
    private final OpenMRSVisitRepository openMRSVisitRepository;
    private final MappingMetaDataRepository mappingMetaDataRepository;
    private static final Logger logger = Logger.getLogger(VisitService.class);

    public VisitService(ConstantsRepository constantsRepository, OpenMRSVisitRepository openMRSVisitRepository, MappingMetaDataRepository mappingMetaDataRepository) {
        this.constantsRepository = constantsRepository;
        this.openMRSVisitRepository = openMRSVisitRepository;
        this.mappingMetaDataRepository = mappingMetaDataRepository;
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

    private OpenMRSVisit createVisit(OpenMRSPatient patient, Enrolment enrolment) {
        String location = constantsRepository.findAllConstants().getValue(ConstantKey.IntegrationBahmniLocation);
        String visitType = constantsRepository.findAllConstants().getValue(ConstantKey.IntegrationBahmniVisitType);
        OpenMRSSaveVisit openMRSSaveVisit = new OpenMRSSaveVisit();
        openMRSSaveVisit.setLocation(location);
        openMRSSaveVisit.setVisitType(visitType);
        openMRSSaveVisit.setPatient(patient.getUuid());
        openMRSSaveVisit.setStartDatetime(patient.getAuditInfo().getDateCreated());
        openMRSSaveVisit.setAttributes(visitAttributes(enrolment));
        OpenMRSVisit visit = openMRSVisitRepository.createVisit(openMRSSaveVisit);
        logger.debug("Created new visit with uuid %s".formatted(visit.getUuid()));
        return visit;
    }

    private List<OpenMRSSaveVisitAttribute> visitAttributes(Enrolment enrolment) {
        String avniIdAttributeType = mappingMetaDataRepository.getBahmniValue(MappingGroup.Common,
                MappingType.AvniUUID_VisitAttributeType);
        var avniIdAttribute = new OpenMRSSaveVisitAttribute();
        avniIdAttribute.setAttributeType(avniIdAttributeType);
        avniIdAttribute.setValue(enrolment.getUuid());

        String avniEventDateAttributeType = mappingMetaDataRepository.getBahmniValue(MappingGroup.Common,
                MappingType.AvniEventDate_VisitAttributeType);
        var eventDateAttribute = new OpenMRSSaveVisitAttribute();
        eventDateAttribute.setAttributeType(avniEventDateAttributeType);
        eventDateAttribute.setValue(FormatAndParseUtil.toISODateString(enrolment.getEnrolmentDateTime()));

        return List.of(avniIdAttribute, eventDateAttribute);
    }

    public OpenMRSVisit getOrCreateVisit(OpenMRSPatient patient) {
        var visit = getVisit(patient.getUuid());
        if (visit == null) {
            return createVisit(patient);
        }
        logger.debug("Retrieved existing visit with uuid %s".formatted(visit.getUuid()));
        return visit;
    }

    public OpenMRSVisit getOrCreateVisit(OpenMRSPatient patient, Enrolment enrolment) {
        var visit = getVisit(patient.getUuid(), enrolment);
        if (visit == null) {
            return createVisit(patient, enrolment);
        }
        logger.debug("Retrieved existing visit with uuid %s".formatted(visit.getUuid()));
        return visit;
    }

    private OpenMRSVisit getVisit(String patientUuid, Enrolment enrolment) {
        String locationUuid = constantsRepository.findAllConstants().getValue(ConstantKey.IntegrationBahmniLocation);
        var visitTypeUuid = mappingMetaDataRepository.getBahmniValue(MappingGroup.ProgramEnrolment,
                MappingType.CommunityEnrolment_VisitType,
                enrolment.getProgram());
        var avniUuidVisitAttributeTypeUuid = mappingMetaDataRepository.getBahmniValue(MappingGroup.Common,
                MappingType.AvniUUID_VisitAttributeType);
        var visits = openMRSVisitRepository.getVisits(patientUuid, locationUuid, visitTypeUuid);
        return visits.stream()
                .filter(visit -> matchesEnrolmentId(visit, enrolment, avniUuidVisitAttributeTypeUuid))
                .findFirst().orElse(null);
    }

    private boolean matchesEnrolmentId(OpenMRSVisit visit, Enrolment enrolment, String avniUuidVisitAttributeTypeUuid) {
        return visit.getAttributes().stream().anyMatch(visitAttribute ->
                visitAttribute.getAttributeType().equals(avniUuidVisitAttributeTypeUuid)
                && visitAttribute.getValue().equals(enrolment.getUuid()));
    }

}