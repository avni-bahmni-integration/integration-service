package org.bahmni_avni_integration.worker.avni;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.contract.avni.Enrolment;
import org.bahmni_avni_integration.contract.internal.SubjectToPatientMetaData;
import org.bahmni_avni_integration.domain.AvniEntityStatus;
import org.bahmni_avni_integration.domain.AvniEntityType;
import org.bahmni_avni_integration.domain.Constants;
import org.bahmni_avni_integration.repository.AvniEntityStatusRepository;
import org.bahmni_avni_integration.repository.avni.AvniEnrolmentRepository;
import org.bahmni_avni_integration.service.EntityStatusService;
import org.bahmni_avni_integration.service.MappingMetaDataService;
import org.bahmni_avni_integration.service.PatientService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Predicate;

@Component
public class EnrolmentWorker {
    private final AvniEntityStatusRepository avniEntityStatusRepository;
    private final AvniEnrolmentRepository avniEnrolmentRepository;

    private static Logger logger = Logger.getLogger(EnrolmentWorker.class);

    public EnrolmentWorker(AvniEntityStatusRepository avniEntityStatusRepository, MappingMetaDataService mappingMetaDataService, AvniEnrolmentRepository avniEnrolmentRepository, PatientService patientService, EntityStatusService entityStatusService) {
        this.avniEntityStatusRepository = avniEntityStatusRepository;
        this.avniEnrolmentRepository = avniEnrolmentRepository;
    }

    public void processEnrolments(Constants constants, Predicate<Enrolment> continueAfterOneRecord) {

        mainLoop: while (true) {
            AvniEntityStatus status = avniEntityStatusRepository.findByEntityType(AvniEntityType.Enrolment);
            Enrolment[] enrolments = avniEnrolmentRepository.getEnrolments(status.getReadUpto());
            logger.info(String.format("Found %d enrolments that are newer than %s", enrolments.length, status.getReadUpto()));
            if (enrolments.length == 0) break;
            for (Enrolment enrolment : enrolments) {
                if (processEnrolment(constants, continueAfterOneRecord, null, enrolment)) {
                    break mainLoop;
                }
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected boolean processEnrolment(Constants constants, Predicate<Enrolment> continueAfterOneRecord, SubjectToPatientMetaData metaData, Enrolment enrolment) {
        logger.info(String.format("Found enrolment %s", enrolment));
        if (!continueAfterOneRecord.test(enrolment)) return true;
        return false;
    }

    public void processEnrolments(Constants constants) {
        this.processEnrolments(constants, enrolment -> true);
    }
}