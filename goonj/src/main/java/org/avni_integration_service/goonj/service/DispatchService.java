package org.avni_integration_service.goonj.service;

import org.avni_integration_service.avni.domain.GeneralEncounter;
import org.avni_integration_service.goonj.config.GoonjMappingDbConstants;
import org.avni_integration_service.goonj.domain.Dispatch;
import org.avni_integration_service.goonj.repository.DispatchRepository;
import org.avni_integration_service.integration_data.domain.IntegrationSystem;
import org.avni_integration_service.integration_data.domain.MappingMetaData;
import org.avni_integration_service.integration_data.repository.IntegrationSystemRepository;
import org.avni_integration_service.integration_data.repository.MappingMetaDataRepository;
import org.avni_integration_service.util.ObsDataType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

import static org.avni_integration_service.goonj.config.GoonjMappingDbConstants.MappingGroup_Dispatch;
import static org.avni_integration_service.goonj.config.GoonjMappingDbConstants.MappingType_Obs;


@Service
public class DispatchService {
    private final DispatchRepository dispatchRepository;
    private final AvniGoonjErrorService avniGoonjErrorService;
    private final MappingMetaDataRepository mappingMetaDataRepository;
    private final IntegrationSystemRepository integrationSystemRepository;

    @Autowired
    public DispatchService(DispatchRepository dispatchRepository, AvniGoonjErrorService avniGoonjErrorService, MappingMetaDataRepository mappingMetaDataRepository, IntegrationSystemRepository integrationSystemRepository) {
        this.dispatchRepository = dispatchRepository;
        this.avniGoonjErrorService = avniGoonjErrorService;
        this.mappingMetaDataRepository = mappingMetaDataRepository;
        this.integrationSystemRepository = integrationSystemRepository;
    }

    public HashMap<String, Object> getDispatch(String uuid) {
        return dispatchRepository.getDispatch(uuid);
    }

    public void populateObservations(GeneralEncounter encounter, Dispatch dispatch) {
        List<String> observationFields = dispatch.getObservationFields();
        IntegrationSystem integrationSystem = integrationSystemRepository.findByName(GoonjMappingDbConstants.IntSystemName);

        for (String obsField : observationFields) {
            MappingMetaData mapping = mappingMetaDataRepository.getAvniMapping(MappingGroup_Dispatch, MappingType_Obs, obsField, integrationSystem);
            ObsDataType dataTypeHint = mapping.getDataTypeHint();
            if (dataTypeHint == null)
                encounter.addObservation(mapping.getAvniValue(), dispatch.getValue(obsField));
            else if (dataTypeHint == ObsDataType.Coded) {
                MappingMetaData answerMapping = mappingMetaDataRepository.getAvniMapping(MappingGroup_Dispatch, MappingType_Obs, dispatch.getValue(obsField).toString(), integrationSystem);
                encounter.addObservation(mapping.getAvniValue(), answerMapping.getAvniValue());
            }
        }
    }
}
