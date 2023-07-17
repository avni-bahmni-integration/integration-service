package org.avni_integration_service.goonj.service;

import org.avni_integration_service.avni.domain.QuestionGroupObservations;
import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.goonj.config.GoonjContextProvider;
import org.avni_integration_service.goonj.domain.Dispatch;
import org.avni_integration_service.goonj.domain.DispatchLineItem;
import org.avni_integration_service.goonj.repository.DispatchRepository;
import org.avni_integration_service.integration_data.repository.IntegrationSystemRepository;
import org.avni_integration_service.integration_data.repository.MappingMetaDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.avni_integration_service.goonj.config.GoonjMappingDbConstants.MappingGroup_Dispatch;
import static org.avni_integration_service.goonj.config.GoonjMappingDbConstants.MappingGroup_Dispatch_LineItem;

@Service
public class DispatchService extends BaseGoonjService {
    private final DispatchRepository dispatchRepository;

    @Autowired
    public DispatchService(DispatchRepository dispatchRepository, AvniGoonjErrorService avniGoonjErrorService, MappingMetaDataRepository mappingMetaDataRepository, GoonjContextProvider goonjContextProvider) {
        super(mappingMetaDataRepository, goonjContextProvider);
        this.dispatchRepository = dispatchRepository;
    }

    public HashMap<String, Object> getDispatch(String uuid) {
        return dispatchRepository.getDispatch(uuid);
    }

    public void populateObservations(Subject subject , Dispatch dispatch) {
        populateObservations(subject, dispatch, MappingGroup_Dispatch);
        List<DispatchLineItem> lineItems = dispatch.getLineItems();
        List<Map<String, Object>> avniLineItems = new ArrayList<>();
        for (DispatchLineItem lineItem : lineItems) {
            QuestionGroupObservations questionGroupObservations = new QuestionGroupObservations();
            populateObservations(questionGroupObservations, lineItem, MappingGroup_Dispatch_LineItem);
            avniLineItems.add(questionGroupObservations.getObservations());
        }
        subject.addObservation("Materials Dispatched", avniLineItems);
    }
}
