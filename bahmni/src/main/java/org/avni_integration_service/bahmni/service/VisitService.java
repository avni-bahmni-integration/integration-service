package org.avni_integration_service.bahmni.service;

import org.apache.log4j.Logger;
import org.avni_integration_service.bahmni.BahmniMappingGroup;
import org.avni_integration_service.bahmni.BahmniMappingType;
import org.avni_integration_service.bahmni.ConstantKey;
import org.avni_integration_service.bahmni.contract.*;
import org.avni_integration_service.avni.domain.Enrolment;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.bahmni.repository.intmapping.MappingService;
import org.avni_integration_service.integration_data.domain.*;
import org.avni_integration_service.integration_data.repository.ConstantsRepository;
import org.avni_integration_service.bahmni.repository.OpenMRSVisitRepository;
import org.avni_integration_service.util.FormatAndParseUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VisitService {
    private final ConstantsRepository constantsRepository;
    private final OpenMRSVisitRepository openMRSVisitRepository;
    private final MappingService mappingService;
    private static final Logger logger = Logger.getLogger(VisitService.class);

    public VisitService(ConstantsRepository constantsRepository, OpenMRSVisitRepository openMRSVisitRepository, MappingService mappingService) {
        this.constantsRepository = constantsRepository;
        this.openMRSVisitRepository = openMRSVisitRepository;
        this.mappingService = mappingService;
    }

    public OpenMRSVisit getAvniRegistrationVisit(String patientUuid) {
        Constants allConstants = constantsRepository.findAllConstants();
        String locationUuid = allConstants.getValue(ConstantKey.IntegrationBahmniLocation.name());
        String visitTypeUuid = allConstants.getValue(ConstantKey.IntegrationBahmniVisitType.name());
        return openMRSVisitRepository.getVisit(patientUuid, locationUuid, visitTypeUuid);
    }

    private OpenMRSVisit getAvniRegistrationVisit(String patientUuid, Enrolment enrolment, String visitTypeUuid) {
        var avniUuidVisitAttributeTypeUuid = mappingService.getBahmniValue(MappingGroup.Common,
                BahmniMappingType.AvniUUID_VisitAttributeType);
        String locationUuid = constantsRepository.findAllConstants().getValue(ConstantKey.IntegrationBahmniLocation.name());
        var visits = openMRSVisitRepository.getVisits(patientUuid, locationUuid, visitTypeUuid);
        return visits.stream()
                .filter(visit -> matchesEnrolmentId(visit, enrolment, avniUuidVisitAttributeTypeUuid))
                .findFirst().orElse(null);
    }

    private OpenMRSVisit createVisit(OpenMRSPatient patient, Subject subject) {
        String location = constantsRepository.findAllConstants().getValue(ConstantKey.IntegrationBahmniLocation.name());
        String visitType = constantsRepository.findAllConstants().getValue(ConstantKey.IntegrationBahmniVisitType.name());
        return createVisit(patient, location, visitType, visitAttributes(subject));
    }

    private OpenMRSVisit createVisit(OpenMRSPatient patient, Enrolment enrolment) {
        String location = constantsRepository.findAllConstants().getValue(ConstantKey.IntegrationBahmniLocation.name());
        String visitType = mappingService.getBahmniValue(BahmniMappingGroup.ProgramEnrolment, BahmniMappingType.CommunityEnrolment_VisitType, enrolment.getProgram());
        return createVisit(patient, location, visitType, visitAttributes(enrolment));
    }

    private OpenMRSVisit createVisit(OpenMRSPatient patient, String location, String visitType, List<OpenMRSSaveVisitAttribute> visitAttributes) {
        OpenMRSSaveVisit openMRSSaveVisit = new OpenMRSSaveVisit();
        openMRSSaveVisit.setLocation(location);
        openMRSSaveVisit.setVisitType(visitType);
        openMRSSaveVisit.setPatient(patient.getUuid());
        String startDatetime = FormatAndParseUtil.toISODateString(
                FormatAndParseUtil.fromIsoDateString(patient.getAuditInfo().getDateCreated()));
        openMRSSaveVisit.setStartDatetime(startDatetime);
        openMRSSaveVisit.setAttributes(visitAttributes);
        OpenMRSVisit visit = openMRSVisitRepository.createVisit(openMRSSaveVisit);
        logger.debug("Created new visit with uuid %s".formatted(visit.getUuid()));
        return visit;
    }

    private List<OpenMRSSaveVisitAttribute> visitAttributes(Subject subject) {
        String avniIdAttributeType = mappingService.getBahmniValue(MappingGroup.Common,
                BahmniMappingType.AvniUUID_VisitAttributeType);
        var avniIdAttribute = new OpenMRSSaveVisitAttribute();
        avniIdAttribute.setAttributeType(avniIdAttributeType);
        avniIdAttribute.setValue(subject.getUuid());

        String avniEventDateAttributeType = mappingService.getBahmniValue(MappingGroup.Common,
                BahmniMappingType.AvniEventDate_VisitAttributeType);
        var eventDateAttribute = new OpenMRSSaveVisitAttribute();
        eventDateAttribute.setAttributeType(avniEventDateAttributeType);
        eventDateAttribute.setValue(FormatAndParseUtil.toISODateString(subject.getRegistrationDate()));

        return List.of(avniIdAttribute, eventDateAttribute);
    }

    private List<OpenMRSSaveVisitAttribute> visitAttributes(Enrolment enrolment) {
        String avniIdAttributeType = mappingService.getBahmniValue(MappingGroup.Common,
                BahmniMappingType.AvniUUID_VisitAttributeType);
        var avniIdAttribute = new OpenMRSSaveVisitAttribute();
        avniIdAttribute.setAttributeType(avniIdAttributeType);
        avniIdAttribute.setValue(enrolment.getUuid());

        String avniEventDateAttributeType = mappingService.getBahmniValue(MappingGroup.Common,
                BahmniMappingType.AvniEventDate_VisitAttributeType);
        var eventDateAttribute = new OpenMRSSaveVisitAttribute();
        eventDateAttribute.setAttributeType(avniEventDateAttributeType);
        eventDateAttribute.setValue(FormatAndParseUtil.toISODateString(enrolment.getEnrolmentDateTime()));

        return List.of(avniIdAttribute, eventDateAttribute);
    }

    public OpenMRSVisit getOrCreateVisit(OpenMRSPatient patient, Subject subject) {
        var visit = getAvniRegistrationVisit(patient.getUuid());
        if (visit == null) {
            return createVisit(patient, subject);
        }
        logger.debug("Retrieved existing visit with uuid %s".formatted(visit.getUuid()));
        return visit;
    }

    public OpenMRSVisit getOrCreateVisit(OpenMRSPatient patient, Enrolment enrolment) {
        var visitTypeUuid = mappingService.getBahmniValue(BahmniMappingGroup.ProgramEnrolment,
                BahmniMappingType.CommunityEnrolment_VisitType,
                enrolment.getProgram());
        var visit = getAvniRegistrationVisit(patient.getUuid(), enrolment, visitTypeUuid);
        if (visit == null) {
            return createVisit(patient, enrolment);
        }
        logger.debug("Retrieved existing visit with uuid %s".formatted(visit.getUuid()));
        return visit;
    }

    private boolean matchesEnrolmentId(OpenMRSVisit visit, Enrolment enrolment, String avniUuidVisitAttributeTypeUuid) {
        return visit.getAttributes().stream().anyMatch(visitAttribute ->
                visitAttribute.getAttributeType().getUuid().equals(avniUuidVisitAttributeTypeUuid)
                && visitAttribute.getValue().equals(enrolment.getUuid()));
    }

    public void voidVisit(Enrolment enrolment, OpenMRSFullEncounter communityEnrolmentEncounter) {
        Constants allConstants = constantsRepository.findAllConstants();
        String locationUuid = allConstants.getValue(ConstantKey.IntegrationBahmniLocation.name());
        String visitType = mappingService.getBahmniValue(BahmniMappingGroup.ProgramEnrolment, BahmniMappingType.CommunityEnrolment_VisitType, enrolment.getProgram());
        OpenMRSVisit visit = openMRSVisitRepository.getVisit(communityEnrolmentEncounter.getPatient().getUuid(), locationUuid, visitType);
        openMRSVisitRepository.deleteVisit(visit.getUuid());
    }
}
