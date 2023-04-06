package org.avni_integration_service.goonj;

import org.apache.log4j.Logger;
import org.avni_integration_service.goonj.config.GoonjMappingDbConstants;
import org.avni_integration_service.integration_data.domain.MappingGroup;
import org.avni_integration_service.integration_data.repository.MappingGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GoonjMappingGroup {
    private static final Logger logger = Logger.getLogger(GoonjMappingGroup.class);
    public final MappingGroup demand;
    public final MappingGroup dispatch;
    public final MappingGroup dispatchLineItem;
    public final MappingGroup dispatchReceipt;
    public final MappingGroup distribution;
    public final MappingGroup activity;
    public final MappingGroup inventory;

    @Autowired
    public GoonjMappingGroup(MappingGroupRepository mappingGroupRepository) {
        this.demand = mappingGroupRepository.findByName(GoonjMappingDbConstants.MappingGroup_Demand);
        this.dispatch = mappingGroupRepository.findByName(GoonjMappingDbConstants.MappingGroup_Dispatch);
        this.dispatchLineItem = mappingGroupRepository.findByName(GoonjMappingDbConstants.MappingGroup_Dispatch_LineItem);
        this.dispatchReceipt = mappingGroupRepository.findByName(GoonjMappingDbConstants.MappingGroup_DispatchReceipt);
        this.distribution = mappingGroupRepository.findByName(GoonjMappingDbConstants.MappingGroup_Distribution);
        this.activity = mappingGroupRepository.findByName(GoonjMappingDbConstants.MappingGroup_Activity);
        this.inventory = mappingGroupRepository.findByName(GoonjMappingDbConstants.MappingGroup_Inventory);
    }
}
