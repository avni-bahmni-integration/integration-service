package org.avni_integration_service.goonj.worker.avni;

import org.apache.log4j.Logger;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.avni.repository.AvniIgnoredConceptsRepository;
import org.avni_integration_service.avni.repository.AvniSubjectRepository;
import org.avni_integration_service.goonj.GoonjEntityType;
import org.avni_integration_service.goonj.GoonjErrorType;
import org.avni_integration_service.goonj.GoonjMappingGroup;
import org.avni_integration_service.goonj.repository.ActivityRepository;
import org.avni_integration_service.goonj.service.AvniGoonjErrorService;
import org.avni_integration_service.integration_data.domain.IntegrationSystem;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.avni_integration_service.integration_data.service.error.ErrorClassifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ActivityWorker extends SubjectWorker {
    private final ActivityRepository activityRepository;
    @Autowired
    public ActivityWorker(AvniSubjectRepository avniSubjectRepository,
                          AvniIgnoredConceptsRepository avniIgnoredConceptsRepository,
                          AvniGoonjErrorService avniGoonjErrorService,
                          IntegratingEntityStatusRepository integrationEntityStatusRepository,
                          ActivityRepository activityRepository,
                          ErrorClassifier errorClassifier, @Qualifier("GoonjIntegrationSystem") IntegrationSystem integrationSystem) {
        super(avniSubjectRepository, avniIgnoredConceptsRepository,
                avniGoonjErrorService, integrationEntityStatusRepository,
                GoonjErrorType.ActivityAttributesMismatch, GoonjEntityType.Activity, Logger.getLogger(ActivityWorker.class),
                errorClassifier, integrationSystem);
        this.activityRepository = activityRepository;
    }
    public void process() throws Exception {
        processSubjects();
    }
    @Override
    protected void createSubject(Subject subject) {
        processActivityEvent(subject);
    }
    private void processActivityEvent(Subject subject) {
        syncSubjectToGoonj(subject, activityRepository, "ActivityId");
    }
}