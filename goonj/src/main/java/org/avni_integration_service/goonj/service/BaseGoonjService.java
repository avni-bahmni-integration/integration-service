package org.avni_integration_service.goonj.service;

import org.avni_integration_service.avni.domain.ObservationHolder;
import org.avni_integration_service.goonj.config.GoonjMappingDbConstants;
import org.avni_integration_service.goonj.domain.GoonjEntity;
import org.avni_integration_service.integration_data.domain.IntegrationSystem;
import org.avni_integration_service.integration_data.domain.MappingMetaData;
import org.avni_integration_service.integration_data.repository.IntegrationSystemRepository;
import org.avni_integration_service.integration_data.repository.MappingMetaDataRepository;
import org.avni_integration_service.util.ObsDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.avni_integration_service.goonj.config.GoonjMappingDbConstants.MappingType_Obs;

public abstract class BaseGoonjService {
    protected static final Logger logger = LoggerFactory.getLogger(BaseGoonjService.class);
    private final MappingMetaDataRepository mappingMetaDataRepository;
    private final IntegrationSystemRepository integrationSystemRepository;

    protected BaseGoonjService(MappingMetaDataRepository mappingMetaDataRepository, IntegrationSystemRepository integrationSystemRepository) {
        this.mappingMetaDataRepository = mappingMetaDataRepository;
        this.integrationSystemRepository = integrationSystemRepository;
    }

    protected void populateObservations(ObservationHolder observationHolder, GoonjEntity goonjEntity, String mappingGroup) {
        List<String> observationFields = goonjEntity.getObservationFields();
        IntegrationSystem integrationSystem = integrationSystemRepository.findByName(GoonjMappingDbConstants.IntSystemName);

        for (String obsField : observationFields) {
            MappingMetaData mapping = mappingMetaDataRepository.getAvniMappingIfPresent(mappingGroup, MappingType_Obs, obsField, integrationSystem);
            if(mapping == null) {
                logger.error("Mapping entry not found for observation field: " + obsField);
                continue;
            }
            ObsDataType dataTypeHint = mapping.getDataTypeHint();
            if (dataTypeHint == null)
                observationHolder.addObservation(mapping.getAvniValue(), goonjEntity.getValue(obsField));
            else if (dataTypeHint == ObsDataType.Coded && goonjEntity.getValue(obsField) != null) {
                MappingMetaData answerMapping = mappingMetaDataRepository.getAvniMappingIfPresent(mappingGroup, MappingType_Obs, goonjEntity.getValue(obsField).toString(), integrationSystem);
                if(answerMapping == null) {
                    logger.error("Answer Mapping entry not found for observation field: " + obsField);
                    continue;
                }
                observationHolder.addObservation(mapping.getAvniValue(), answerMapping.getAvniValue());
            }
        }
    }
}
