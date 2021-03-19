package org.bahmni_avni_integration.worker.avni;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.contract.avni.Enrolment;
import org.bahmni_avni_integration.contract.avni.Subject;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSFullEncounter;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSUuidHolder;
import org.bahmni_avni_integration.integration_data.internal.AvniToBahmniEnrolmentMetaData;
import org.bahmni_avni_integration.integration_data.internal.SubjectToPatientMetaData;
import org.bahmni_avni_integration.integration_data.domain.AvniEntityStatus;
import org.bahmni_avni_integration.integration_data.domain.AvniEntityType;
import org.bahmni_avni_integration.integration_data.domain.Constants;
import org.bahmni_avni_integration.integration_data.repository.AvniEntityStatusRepository;
import org.bahmni_avni_integration.integration_data.repository.avni.AvniEnrolmentRepository;
import org.bahmni_avni_integration.integration_data.repository.avni.AvniSubjectRepository;
import org.bahmni_avni_integration.service.EnrolmentService;
import org.bahmni_avni_integration.service.EntityStatusService;
import org.bahmni_avni_integration.service.MappingMetaDataService;
import org.bahmni_avni_integration.service.PatientService;
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
    private final PatientService patientService;

    private static Logger logger = Logger.getLogger(EnrolmentWorker.class);
    private final AvniSubjectRepository avniSubjectRepository;
    private final MappingMetaDataService mappingMetaDataService;

    public EnrolmentWorker(AvniEntityStatusRepository avniEntityStatusRepository, MappingMetaDataService mappingMetaDataService, AvniEnrolmentRepository avniEnrolmentRepository, EntityStatusService entityStatusService, EnrolmentService enrolmentService, PatientService patientService, AvniSubjectRepository avniSubjectRepository) {
        this.avniEntityStatusRepository = avniEntityStatusRepository;
        this.mappingMetaDataService = mappingMetaDataService;
        this.avniEnrolmentRepository = avniEnrolmentRepository;
        this.entityStatusService = entityStatusService;
        this.enrolmentService = enrolmentService;
        this.patientService = patientService;
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
        AvniToBahmniEnrolmentMetaData enrolmentMetaData = mappingMetaDataService.getForAvniToBahmniEnrolment();
        logger.debug(String.format("Processing avni enrolment %s", enrolment.getUuid()));
        Subject subject = avniSubjectRepository.getSubject(enrolment.getSubjectId());
        logger.debug(String.format("Found avni subject %s", subject.getUuid()));
        Pair<OpenMRSUuidHolder, OpenMRSFullEncounter> patientEncounter = enrolmentService.findCommunityEnrolment(enrolment, subject, constants, metaData);
        OpenMRSUuidHolder patient = patientEncounter.getValue0();
        OpenMRSFullEncounter encounter = patientEncounter.getValue1();

        if (patient != null && encounter == null) {
            logger.debug(String.format("Creating new Bahmni Enrolment for Avni enrolment %s", enrolment.getUuid()));
            enrolmentService.createCommunityEnrolment(enrolment, patient, constants);
        } else if (patient != null && encounter != null) {
            logger.debug(String.format("Updating existing Bahmni encounter %s", encounter.getUuid()));
            enrolmentService.updateCommunityEnrolment(encounter, enrolment, constants);
        } else if (patient == null && encounter == null) {
            logger.debug(String.format("Patient with identifier %s not found", subject.getId(metaData)));
            enrolmentService.processPatientNotFound(enrolment, subject, metaData);
        }

        entityStatusService.saveEntityStatus(enrolment);
        if (!continueAfterOneRecord.test(enrolment)) return true;
        return false;
    }

    public void processEnrolments(Constants constants) {
        this.processEnrolments(constants, enrolment -> true);
    }
}