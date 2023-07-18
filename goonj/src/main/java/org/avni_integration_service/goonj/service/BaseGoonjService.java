package org.avni_integration_service.goonj.service;

import org.avni_integration_service.avni.domain.ObservationHolder;
import org.avni_integration_service.goonj.config.GoonjContextProvider;
import org.avni_integration_service.goonj.config.GoonjMappingDbConstants;
import org.avni_integration_service.goonj.domain.GoonjEntity;
import org.avni_integration_service.integration_data.domain.IntegrationSystem;
import org.avni_integration_service.integration_data.domain.MappingMetaData;
import org.avni_integration_service.integration_data.domain.framework.MappingException;
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
    private final GoonjContextProvider goonjContextProvider;

    protected BaseGoonjService(MappingMetaDataRepository mappingMetaDataRepository, GoonjContextProvider goonjContextProvider) {
        this.mappingMetaDataRepository = mappingMetaDataRepository;
        this.goonjContextProvider = goonjContextProvider;
    }

    protected void populateObservations(ObservationHolder observationHolder, GoonjEntity goonjEntity, String mappingGroup) {
        List<String> observationFields = goonjEntity.getObservationFields();
        for (String obsField : observationFields) {
            MappingMetaData mapping = mappingMetaDataRepository.getAvniMappingIfPresent(mappingGroup, MappingType_Obs, obsField, goonjContextProvider.get().getIntegrationSystem().getId());
            if(mapping == null) {
                logger.error("Mapping entry not found for observation field: " + obsField);
                continue;
            }
            ObsDataType dataTypeHint = mapping.getDataTypeHint();
            if (dataTypeHint == null)
                observationHolder.addObservation(mapping.getAvniValue(), goonjEntity.getValue(obsField));
            else if (dataTypeHint == ObsDataType.Coded && goonjEntity.getValue(obsField) != null) {
                MappingMetaData answerMapping = mappingMetaDataRepository.getAvniMappingIfPresent(mappingGroup, MappingType_Obs, goonjEntity.getValue(obsField).toString(), goonjContextProvider.get().getIntegrationSystem().getId());
                if(answerMapping == null) {
                    String errorMessage = "Answer Mapping entry not found for coded concept answer field: " + obsField;
                    logger.error(errorMessage);
                    throw new MappingException(errorMessage);
                }
                observationHolder.addObservation(mapping.getAvniValue(), answerMapping.getAvniValue());
            }
        }
    }
}
