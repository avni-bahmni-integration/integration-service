package org.avni_integration_service.goonj.service;

import org.avni_integration_service.avni.domain.Subject;
import org.avni_integration_service.goonj.config.GoonjMappingDbConstants;
import org.avni_integration_service.goonj.domain.Demand;
import org.avni_integration_service.goonj.repository.DemandRepository;
import org.avni_integration_service.integration_data.domain.IntegrationSystem;
import org.avni_integration_service.integration_data.domain.MappingMetaData;
import org.avni_integration_service.integration_data.repository.IntegrationSystemRepository;
import org.avni_integration_service.integration_data.repository.MappingMetaDataRepository;
import org.avni_integration_service.util.ObsDataType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

import static org.avni_integration_service.goonj.config.GoonjMappingDbConstants.MappingGroup_Demand;
import static org.avni_integration_service.goonj.config.GoonjMappingDbConstants.MappingType_Obs;

@Service
public class DemandService {
    private final DemandRepository demandRepository;
    private final AvniGoonjErrorService avniGoonjErrorService;
    private final MappingMetaDataRepository mappingMetaDataRepository;
    private final IntegrationSystemRepository integrationSystemRepository;

    @Autowired
    public DemandService(DemandRepository demandRepositoryGoonj, AvniGoonjErrorService avniGoonjErrorService, MappingMetaDataRepository mappingMetaDataRepository, IntegrationSystemRepository integrationSystemRepository) {
        this.demandRepository = demandRepositoryGoonj;
        this.avniGoonjErrorService = avniGoonjErrorService;
        this.mappingMetaDataRepository = mappingMetaDataRepository;
        this.integrationSystemRepository = integrationSystemRepository;
    }

    public HashMap<String, Object> getDemand(String uuid) {
        return demandRepository.getDemand(uuid);
    }

    public void populateObservations(Subject subject, Demand demand) {
        List<String> observationFields = demand.getObservationFields();
        IntegrationSystem integrationSystem = integrationSystemRepository.findByName(GoonjMappingDbConstants.IntSystemName);

        for (String obsField : observationFields) {
            MappingMetaData mapping = mappingMetaDataRepository.getAvniMapping(MappingGroup_Demand, MappingType_Obs, obsField, integrationSystem);
            if (mapping == null) {
                // use convention
                subject.addObservation(obsField, demand.getValue(obsField));
            } else {
                ObsDataType dataTypeHint = mapping.getDataTypeHint();
                if (dataTypeHint == null)
                    subject.addObservation(mapping.getAvniValue(), demand.getValue(obsField));
                else if (dataTypeHint == ObsDataType.Coded) {
                    MappingMetaData answerMapping = mappingMetaDataRepository.getAvniMapping(MappingGroup_Demand, MappingType_Obs, demand.getValue(obsField).toString(), integrationSystem);
                    if (answerMapping == null) {
                        // use convention for coded answer
                        subject.addObservation(mapping.getAvniValue(), demand.getValue(obsField));
                    } else {
                        subject.addObservation(mapping.getAvniValue(), answerMapping.getAvniValue());
                    }
                }
            }
        }
    }
}
