package org.avni_integration_service.goonj.service;

import org.avni_integration_service.goonj.repository.DemandRepositoryGoonj;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class DemandService {

    private final GoonjSubjectMapper goonjSubjectMapper;
    private final DemandRepositoryGoonj demandRepositoryGoonj;
    private final AvniGoonjErrorService avniGoonjErrorService;

    @Autowired
    public DemandService(GoonjSubjectMapper goonjSubjectMapper, DemandRepositoryGoonj demandRepositoryGoonj, AvniGoonjErrorService avniGoonjErrorService) {
        this.goonjSubjectMapper = goonjSubjectMapper;
        this.demandRepositoryGoonj = demandRepositoryGoonj;
        this.avniGoonjErrorService = avniGoonjErrorService;
    }


    public HashMap<String, Object> getDemand(String uuid) {
        return demandRepositoryGoonj.getDemand(uuid);
    }
}
