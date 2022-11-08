package org.avni_integration_service.amrit.worker;

import org.apache.log4j.Logger;
import org.avni_integration_service.amrit.service.AvniAmritErrorService;
import org.avni_integration_service.amrit.service.BeneficiaryService;
import org.avni_integration_service.avni.repository.AvniSubjectRepository;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.springframework.stereotype.Component;

@Component
public class BeneficiaryWorker {
    private final BeneficiaryService beneficiaryService;
    private static final Logger logger = Logger.getLogger(BeneficiaryWorker.class);
    private final IntegratingEntityStatusRepository integratingEntityStatusRepository;
    private final AvniSubjectRepository avniSubjectRepository;
    private final AvniAmritErrorService avniAmritErrorService;

    public BeneficiaryWorker(BeneficiaryService beneficiaryService, IntegratingEntityStatusRepository integratingEntityStatusRepository, AvniSubjectRepository avniSubjectRepository, AvniAmritErrorService avniAmritErrorService) {
        this.beneficiaryService = beneficiaryService;
        this.integratingEntityStatusRepository = integratingEntityStatusRepository;
        this.avniSubjectRepository = avniSubjectRepository;
        this.avniAmritErrorService = avniAmritErrorService;
    }

    //TODO
    public void syncBeneficiariesFromAvniToAmrit() {

    }

}
