package org.avni_integration_service.amrit.worker;

import org.apache.log4j.Logger;
import org.avni_integration_service.amrit.config.AmritEntityType;
import org.avni_integration_service.amrit.config.AmritErrorType;
import org.avni_integration_service.amrit.service.AvniAmritErrorService;
import org.avni_integration_service.amrit.service.BeneficiaryService;
import org.avni_integration_service.amrit.service.BornBirthService;
import org.avni_integration_service.amrit.service.CBACService;
import org.avni_integration_service.avni.domain.GeneralEncounter;
import org.avni_integration_service.avni.domain.GeneralEncountersResponse;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.avni.repository.AvniEncounterRepository;
import org.avni_integration_service.avni.repository.AvniSubjectRepository;
import org.avni_integration_service.integration_data.domain.AvniEntityType;
import org.avni_integration_service.integration_data.domain.IntegratingEntityStatus;
import org.avni_integration_service.integration_data.domain.error.ErrorRecord;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.avni_integration_service.integration_data.service.IntegratingEntityStatusService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Component
public class AmritEncounterWorker implements ErrorRecordWorker {
    private static final Logger logger = Logger.getLogger(AmritEncounterWorker.class);
    private final BeneficiaryService beneficiaryService;
    private final BornBirthService bornBirthService;
    private final CBACService cBACService;
    private final AvniEncounterRepository avniEncounterRepository;
    private final AvniSubjectRepository avniSubjectRepository;
    private final IntegratingEntityStatusRepository integratingEntityStatusRepository;
    private final IntegratingEntityStatusService integratingEntityStatusService;
    private final AvniAmritErrorService avniAmritErrorService;
    private final AmritEntityType amritEntityType;

    public AmritEncounterWorker(BeneficiaryService beneficiaryService,
                                BornBirthService bornBirthService, CBACService cBACService,
                                AvniEncounterRepository avniEncounterRepository,
                                IntegratingEntityStatusRepository integratingEntityStatusRepository,
                                AvniSubjectRepository avniSubjectRepository,
                                IntegratingEntityStatusService integratingEntityStatusService,
                                AvniAmritErrorService avniAmritErrorService) {
        this.beneficiaryService = beneficiaryService;
        this.bornBirthService = bornBirthService;
        this.cBACService = cBACService;
        this.avniEncounterRepository = avniEncounterRepository;
        this.integratingEntityStatusRepository = integratingEntityStatusRepository;
        this.avniSubjectRepository = avniSubjectRepository;
        this.integratingEntityStatusService = integratingEntityStatusService;
        this.avniAmritErrorService = avniAmritErrorService;
        this.amritEntityType = AmritEntityType.Beneficiary;
    }

    public void syncEncountersFromAvniToAmrit(AmritEntityType entityType) {
        processEncounters(entityType);
    }

    public void processEncounters(AmritEntityType entityType) {
        while (true) {
            IntegratingEntityStatus beneficiarySyncStatus = integratingEntityStatusRepository.findByEntityType(entityType.name());
            IntegratingEntityStatus status = integratingEntityStatusRepository.findByEntityType(entityType.name());
            Date readUptoDateTime = getEffectiveCutoffDateTime(status);
            GeneralEncountersResponse response = avniEncounterRepository.getGeneralEncounters(readUptoDateTime, entityType.getDbName());
            GeneralEncounter[] generalEncounters = response.getContent();
            int totalPages = response.getTotalPages();
            logger.info(String.format("Found %d generalEncounters that are newer than %s", generalEncounters.length, readUptoDateTime));
            if (generalEncounters.length == 0) break;
            for (GeneralEncounter encounter : generalEncounters) {
                processEncounter(encounter, true, entityType);
            }
            if (totalPages == 1) {
                logger.info("Finished processing all pages");
                break;
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void processEncounter(GeneralEncounter encounter, boolean updateSyncStatus, AmritEntityType entityType) {
        logger.debug("%s Processing encounter  %s".formatted(entityType, encounter.getUuid()));
        if (shouldFilterEncounter(encounter)) {
            logger.warn(String.format("%s General encounter should be filtered out: %s", entityType, encounter.getUuid()));
            updateSyncStatus(encounter, updateSyncStatus, entityType);
            return;
        }

        Subject beneficiary = avniSubjectRepository.getSubject(encounter.getSubjectId());
        logger.debug(String.format("Found avni beneficiary %s", beneficiary.getUuid()));
        if (beneficiary.getVoided()) {
            logger.debug(String.format("Avni beneficiary is voided. Skipping. %s", beneficiary.getUuid()));
            updateSyncStatus(encounter, updateSyncStatus, entityType);
            return;
        }

       if(entityType.equals(AmritEntityType.CBAC)) {
            cBACService.createOrUpdateCBAC(beneficiary, encounter);
        } else {
            throw new AssertionError(String.format("Cannot process Avni encounter for entityType %s", entityType));
        }

        updateSyncStatus(encounter, updateSyncStatus, entityType);
    }

    private boolean shouldFilterEncounter(GeneralEncounter generalEncounter) {
        return !generalEncounter.isCompleted() || generalEncounter.getVoided();
    }

    private void updateSyncStatus(GeneralEncounter encounter, boolean updateSyncStatus, AmritEntityType entityType) {
        if (updateSyncStatus) {
            integratingEntityStatusService.saveEntityStatus(entityType.name(), encounter.getLastModifiedDate());
        }
    }

    @Override
    public void processError(ErrorRecord errorRecord) {
        String entityUuid = errorRecord.getEntityId();
        GeneralEncounter encounter = avniEncounterRepository.getGeneralEncounter(entityUuid);
        if (encounter == null) {
            String message = String.format("%s encounter has been deleted now: %s", errorRecord.getIntegratingEntityType(), entityUuid);
            logger.warn(message);
            avniAmritErrorService.errorOccurred(entityUuid, AmritErrorType.EntityIsDeleted, AvniEntityType.GeneralEncounter, message);
            return;
        }

        if(errorRecord.getIntegratingEntityType() != null) {
            if(errorRecord.getIntegratingEntityType().equals(AmritEntityType.BornBirth.name())) {
                processEncounter(encounter, false, AmritEntityType.BornBirth);
            } else if(errorRecord.getIntegratingEntityType().equals(AmritEntityType.CBAC.name())) {
                processEncounter(encounter, false, AmritEntityType.CBAC);
            } else {
                throw new AssertionError(String.format("Cannot handle error record with AvniEntityType=%s / AmritEntityType=%s", errorRecord.getAvniEntityType(), errorRecord.getIntegratingEntityType()));
            }
        } else {
            throw new AssertionError(String.format("Invalid error record with AvniEntityType=%s / AmritEntityType=%s", errorRecord.getAvniEntityType(), errorRecord.getIntegratingEntityType()));
        }
    }
}
