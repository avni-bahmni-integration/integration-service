package org.avni_integration_service.amrit.worker;

import org.apache.log4j.Logger;
import org.avni_integration_service.amrit.config.AmritEntityType;
import org.avni_integration_service.amrit.config.AmritErrorType;
import org.avni_integration_service.amrit.config.HouseholdConstants;
import org.avni_integration_service.amrit.service.AvniAmritErrorService;
import org.avni_integration_service.amrit.service.HouseholdService;
import org.avni_integration_service.avni.domain.Household;
import org.avni_integration_service.avni.domain.HouseholdResponse;
import org.avni_integration_service.avni.repository.AvniSubjectRepository;
import org.avni_integration_service.integration_data.domain.AvniEntityType;
import org.avni_integration_service.integration_data.domain.IntegratingEntityStatus;
import org.avni_integration_service.integration_data.domain.error.ErrorRecord;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.avni_integration_service.integration_data.service.IntegratingEntityStatusService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class HouseholdWorker implements HouseholdConstants, ErrorRecordWorker {
    private static final Logger logger = Logger.getLogger(HouseholdWorker.class);
    private final HouseholdService householdService;
    private final AvniSubjectRepository avniSubjectRepository;
    private final IntegratingEntityStatusRepository integratingEntityStatusRepository;
    private final IntegratingEntityStatusService integratingEntityStatusService;
    private final AvniAmritErrorService avniAmritErrorService;
    private final AmritEntityType amritEntityType;

    public HouseholdWorker(HouseholdService householdService,
                           IntegratingEntityStatusRepository integratingEntityStatusRepository,
                           AvniSubjectRepository avniSubjectRepository,
                           IntegratingEntityStatusService integratingEntityStatusService,
                           AvniAmritErrorService avniAmritErrorService) {
        this.householdService = householdService;
        this.integratingEntityStatusRepository = integratingEntityStatusRepository;
        this.avniSubjectRepository = avniSubjectRepository;
        this.integratingEntityStatusService = integratingEntityStatusService;
        this.avniAmritErrorService = avniAmritErrorService;
        this.amritEntityType = AmritEntityType.Beneficiary;
    }

    public void syncHouseholdsFromAvniToAmrit() {
        processHouseholds();
    }

    public void processHouseholds() {
        while (true) {
            IntegratingEntityStatus beneficiarySyncStatus = integratingEntityStatusRepository.findByEntityType(AmritEntityType.Household.name());
            IntegratingEntityStatus status = integratingEntityStatusRepository.findByEntityType(AmritEntityType.Household.name());
            HouseholdResponse response = avniSubjectRepository.getGroupSubjects(status.getReadUptoDateTime(), SUBJECT_TYPE);
            Household[] households = response.getContent();
            int totalPages = response.getTotalPages();
            logger.info(String.format("Found %d households that are newer than %s", households.length, status.getReadUptoDateTime()));
            if (households.length == 0) break;
            for (Household household : households) {
                processHousehold(household, true);
            }
            if (totalPages == 1) {
                logger.info("Finished processing all pages");
                break;
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void processHousehold(Household household, boolean updateSyncStatus) {
        logger.debug("Processing household  %s".formatted(household.getGroupSubject().getUuid()));
        householdService.createOrUpdateHousehold(household);
        updateSyncStatus(household, updateSyncStatus);
    }

    private void updateSyncStatus(Household household, boolean updateSyncStatus) {
        if (updateSyncStatus) {//TODO check if getLastModifiedDate stored has valid time component and can be used in next sync
            integratingEntityStatusService.saveEntityStatus(AmritEntityType.Household.name(), household.getLastModifiedDate());
        }
    }

    @Override
    public void processError(ErrorRecord errorRecord) {
        String entityUuid = errorRecord.getEntityId();
        Household household = avniSubjectRepository.getHousehold(entityUuid);
        if (household == null) {
            String message = String.format("Household has been deleted now: %s", entityUuid);
            logger.warn(message);
            avniAmritErrorService.errorOccurred(entityUuid, AmritErrorType.EntityIsDeleted, AvniEntityType.Subject, message);
            return;
        }
        processHousehold(household, false);
    }
}
