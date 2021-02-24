package org.bahmni_avni_integration.worker.avni;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.contract.avni.Enrolment;
import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSUuidHolder;
import org.bahmni_avni_integration.contract.internal.SubjectToPatientMetaData;
import org.bahmni_avni_integration.domain.AvniEntityStatus;
import org.bahmni_avni_integration.domain.AvniEntityType;
import org.bahmni_avni_integration.domain.Constants;
import org.bahmni_avni_integration.repository.AvniEntityStatusRepository;
import org.bahmni_avni_integration.repository.avni.AvniEnrolmentRepository;
import org.bahmni_avni_integration.repository.avni.AvniSubjectRepository;
import org.bahmni_avni_integration.service.EnrolmentService;
import org.bahmni_avni_integration.service.EntityStatusService;
import org.bahmni_avni_integration.service.MappingMetaDataService;
import org.javatuples.Pair;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Predicate;

@Component
public class EnrolmentWorker {
    private final AvniEntityStatusRepository avniEntityStatusRepository;
    private final AvniEnrolmentRepository avniEnrolmentRepository;
    private final EntityStatusService entityStatusService;
    private final EnrolmentService enrolmentService;

    private static Logger logger = Logger.getLogger(EnrolmentWorker.class);
    private final AvniSubjectRepository avniSubjectRepository;
    private final MappingMetaDataService mappingMetaDataService;

    public EnrolmentWorker(AvniEntityStatusRepository avniEntityStatusRepository, MappingMetaDataService mappingMetaDataService, AvniEnrolmentRepository avniEnrolmentRepository, EntityStatusService entityStatusService, EnrolmentService enrolmentService, AvniSubjectRepository avniSubjectRepository) {
        this.avniEntityStatusRepository = avniEntityStatusRepository;
        this.mappingMetaDataService = mappingMetaDataService;
        this.avniEnrolmentRepository = avniEnrolmentRepository;
        this.entityStatusService = entityStatusService;
        this.enrolmentService = enrolmentService;
        this.avniSubjectRepository = avniSubjectRepository;
    }

    public void processEnrolments(Constants constants, Predicate<Enrolment> continueAfterOneRecord) {
        SubjectToPatientMetaData subjectToPatientMetaData = mappingMetaDataService.getForSubjectToPatient();
        mainLoop:
        while (true) {
            AvniEntityStatus status = avniEntityStatusRepository.findByEntityType(AvniEntityType.Enrolment);
            Enrolment[] enrolments = avniEnrolmentRepository.getEnrolments(status.getReadUpto());
            logger.info(String.format("Found %d enrolments that are newer than %s", enrolments.length, status.getReadUpto()));
            if (enrolments.length == 0) break;
            for (Enrolment enrolment : enrolments) {
                if (processEnrolment(constants, continueAfterOneRecord, subjectToPatientMetaData, enrolment))
                    break mainLoop;
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected boolean processEnrolment(Constants constants, Predicate<Enrolment> continueAfterOneRecord, SubjectToPatientMetaData metaData, Enrolment enrolment) {
        logger.debug(String.format("Processing avni enrolment %s", enrolment));
        Subject subject = avniSubjectRepository.getSubject(enrolment.getSubjectId());
        logger.debug(String.format("Found avni subject %s", subject));
        Pair<OpenMRSUuidHolder, OpenMRSEncounter> patientEncounter = enrolmentService.findCommunityEnrolment(enrolment, subject, constants, metaData);
        OpenMRSUuidHolder patient = patientEncounter.getValue0();
        OpenMRSEncounter encounter = patientEncounter.getValue1();

        if (patient != null && encounter == null) {
            enrolmentService.createCommunityEnrolment(enrolment, patient, constants);
        } else if (patient != null && encounter != null) {
            enrolmentService.updateCommunityEnrolment(encounter, enrolment, patient, constants);
        } else if (patient == null && encounter == null) {
            enrolmentService.processPatientNotFound(subject, metaData);
        }

        entityStatusService.saveEntityStatus(enrolment);
        if (!continueAfterOneRecord.test(enrolment)) return true;
        return false;
    }

    public void processEnrolments(Constants constants) {
        this.processEnrolments(constants, enrolment -> true);
    }
}