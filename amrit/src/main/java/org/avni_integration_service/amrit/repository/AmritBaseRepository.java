package org.avni_integration_service.amrit.repository;

import org.apache.log4j.Logger;
import org.avni_integration_service.amrit.config.AmritApplicationConfig;
import org.avni_integration_service.amrit.config.AmritMappingDbConstants;
import org.avni_integration_service.amrit.dto.AmritBaseResponse;
import org.avni_integration_service.amrit.dto.AmritFetchIdentityResponse;
import org.avni_integration_service.amrit.util.DateTimeUtil;
import org.avni_integration_service.avni.domain.AvniBaseContract;
import org.avni_integration_service.avni.domain.GeneralEncounter;
import org.avni_integration_service.integration_data.domain.IntegrationSystem;
import org.avni_integration_service.integration_data.domain.MappingGroup;
import org.avni_integration_service.integration_data.domain.MappingMetaData;
import org.avni_integration_service.integration_data.domain.MappingType;
import org.avni_integration_service.integration_data.repository.*;
import org.avni_integration_service.util.ObjectJsonMapper;
import org.avni_integration_service.util.ObsDataType;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.*;

import java.net.URI;
import java.util.*;

import static org.avni_integration_service.amrit.config.AmritMappingDbConstants.MAPPING_GROUP_MASTER_IDS;

public abstract class AmritBaseRepository {
    private static final Logger logger = Logger.getLogger(AmritBaseRepository.class);
    private static final String DELETION_RECORD_ID = "recordId";
    private static final String DELETION_SOURCE_ID = "sourceId";
    private static final String FETCH_AMRIT_ID_RESOURCE_PATH = "/rmnch/getAmritIdForAvniId";
    protected final AmritApplicationConfig amritApplicationConfig;
    private final IntegratingEntityStatusRepository integratingEntityStatusRepository;
    private final IntegrationSystem integrationSystem;
    private final MappingGroupRepository mappingGroupRepository;
    private final MappingTypeRepository mappingTypeRepository;
    private final MappingMetaDataRepository mappingMetaDataRepository;
    private final RestTemplate amritRestTemplate;
    private final String entityType;

    public AmritBaseRepository(IntegratingEntityStatusRepository integratingEntityStatusRepository,
                               MappingGroupRepository mappingGroupRepository, RestTemplate restTemplate,
                               AmritApplicationConfig amritApplicationConfig, MappingMetaDataRepository mappingMetaDataRepository,
                               IntegrationSystemRepository integrationSystemRepository, MappingTypeRepository mappingTypeRepository,
                               String entityType) {
        this.integratingEntityStatusRepository = integratingEntityStatusRepository;
        this.mappingGroupRepository = mappingGroupRepository;
        this.amritRestTemplate = restTemplate;
        this.amritApplicationConfig = amritApplicationConfig;
        this.mappingTypeRepository = mappingTypeRepository;
        this.entityType = entityType;
        this.mappingMetaDataRepository = mappingMetaDataRepository;
        this.integrationSystem = integrationSystemRepository.findByName(AmritMappingDbConstants.IntSystemName);
    }

    private <T extends AmritBaseResponse> boolean extractResponse(ResponseEntity<T> responseEntity) {
        if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
            HttpStatus actualStatus = HttpStatus.resolve((int) responseEntity.getBody().getStatusCode());
            if (actualStatus != null && actualStatus.is2xxSuccessful()) {
                return true;
            } else {
                throw handleError(responseEntity, HttpStatus.BAD_REQUEST);
            }
        }
        return false;
    }

    protected <T extends AmritBaseResponse> T getResponse(Date dateTime, String resource, Class<T> returnType) {
        URI uri = URI.create(String.format("%s/%s?dateTimestamp=%s", amritApplicationConfig.getAmritServerUrl(), resource, DateTimeUtil.formatDateTime(dateTime)));
        ResponseEntity<T> responseEntity = amritRestTemplate.exchange(uri, HttpMethod.GET, null, returnType);
        if (extractResponse(responseEntity)) return responseEntity.getBody();
        logger.error(String.format("Failed to fetch data for resource %s, response status code is %s", resource, responseEntity.getStatusCode()));
        throw new HttpServerErrorException(responseEntity.getStatusCode());
    }

    /**
     * All our Sync time-stamps for Amrit, i.e. Beneficiary, CBAC Form, etc..
     * are stored using integrating_entity_status DB and none in avniEntityStatus table.
     *
     * @return cutOffDate
     */
    protected Date getCutOffDate() {
        return integratingEntityStatusRepository.findByEntityType(entityType).getReadUptoDateTime();
    }

    protected Date getCutOffDateTime() {
        return getCutOffDate();
    }

    protected <T extends AmritBaseResponse> T getSingleEntityResponse(String resource, HttpEntity<?> requestEntity, Class<T> returnType) {
        URI uri = URI.create(String.format("%s/%s", amritApplicationConfig.getAmritServerUrl(), resource));
        ResponseEntity<T> responseEntity = amritRestTemplate.exchange(uri, HttpMethod.POST, requestEntity, returnType);
        if (extractResponse(responseEntity)) return responseEntity.getBody();
        logger.error(String.format("Failed to fetch data for resource %s, response status code is %s", resource, responseEntity.getStatusCode()));
        throw getRestClientResponseException(responseEntity.getHeaders(), responseEntity.getStatusCode(), null);
    }

    protected <T extends AmritBaseResponse> T createSingleEntity(String resource, HttpEntity<?> requestEntity,Class<T> returnType) throws RestClientResponseException {
        logger.info("Request body:" + ObjectJsonMapper.writeValueAsString(requestEntity.getBody()));
        URI uri = URI.create(String.format("%s/%s", amritApplicationConfig.getAmritServerUrl(), resource));
        ResponseEntity<T> responseEntity = amritRestTemplate.exchange(uri, HttpMethod.POST, requestEntity, returnType);
        if (extractResponse(responseEntity)) return responseEntity.getBody();
        logger.error(String.format("Failed to create resource %s,  response status code is %s", resource, responseEntity.getStatusCode()));
        throw handleError(responseEntity, responseEntity.getStatusCode());
    }

    protected AmritBaseResponse deleteSingleEntity(String resource, HttpEntity<?> requestEntity) throws RestClientResponseException {
        logger.info("Request body:" + ObjectJsonMapper.writeValueAsString(requestEntity.getBody()));
        URI uri = URI.create(String.format("%s/%s", amritApplicationConfig.getAmritServerUrl(), resource));
        ResponseEntity<AmritBaseResponse> responseEntity = amritRestTemplate.exchange(uri, HttpMethod.DELETE, requestEntity, AmritBaseResponse.class);
        if (extractResponse(responseEntity)) return responseEntity.getBody();
        logger.error(String.format("Failed to delete resource %s, response error message is %s", resource, responseEntity.getBody()));
        throw handleError(responseEntity, responseEntity.getStatusCode());
    }

    protected RestClientException handleError(ResponseEntity<? extends AmritBaseResponse> responseEntity, HttpStatus statusCode) {
        AmritBaseResponse responseBody = responseEntity.getBody();
        return getRestClientResponseException(responseEntity.getHeaders(), statusCode, responseBody.getErrorMessage());
    }

    protected void populateObservations(Map<String, Object> observationHolder, AvniBaseContract avniEntity,
                                        String mappingGroup, String mappingType, String mappingTypeCodedObservations) {
        MappingGroup mappingGroupEntity = mappingGroupRepository.findByName(mappingGroup);
        MappingType mappingTypeEntity = mappingTypeRepository.findByName(mappingType);
        List<MappingMetaData> amritFields = mappingMetaDataRepository.findAllByMappingGroupAndMappingType(mappingGroupEntity, mappingTypeEntity);


        for (MappingMetaData amritField : amritFields) {
            MappingMetaData mapping = mappingMetaDataRepository
                    .getAvniMappingIfPresent(mappingGroup, mappingType, amritField.getIntSystemValue(), integrationSystem);
            if(mapping == null) {
                logger.warn("Mapping entry not found for amrit field: " + amritField.getIntSystemValue());
                continue;
            }
            String obsField = mapping.getAvniValue();
            ObsDataType dataTypeHint = mapping.getDataTypeHint();
            if (dataTypeHint == null) {
                observationHolder.put(mapping.getIntSystemValue(), getValue(avniEntity, obsField));
            } else if (dataTypeHint == ObsDataType.Coded && getValue(avniEntity, obsField) != null) {
                MappingMetaData answerMapping = mappingMetaDataRepository.getIntSystemMappingIfPresent(mappingGroup,
                        mappingTypeCodedObservations, getValue(avniEntity, obsField).toString(), integrationSystem);
                if(answerMapping != null) {
                    observationHolder.put(mapping.getIntSystemValue(), answerMapping.getIntSystemValue());
                } else {
                    logger.error(String.format("Unable to find coded mapping for attribute %s", mapping.getIntSystemValue()));
                }
            } else if (dataTypeHint == ObsDataType.Numeric && getValue(avniEntity, obsField) != null) {
                //Fetch corresponding ID from group MAPPING_GROUP_MASTER_IDS for the same mappingType
                MappingMetaData answerMapping = mappingMetaDataRepository.getIntSystemMappingIfPresent(MAPPING_GROUP_MASTER_IDS,
                        mapping.getIntSystemValue(), getValue(avniEntity, obsField).toString(), integrationSystem);
                if(answerMapping != null) {
                    observationHolder.put(mapping.getIntSystemValue(), answerMapping.getIntSystemValue());
                } else {
                    logger.error(String.format("Unable to find numeric mapping for attribute %s", mapping.getIntSystemValue()));
                }
            } else if (dataTypeHint == ObsDataType.Text && getValue(avniEntity, obsField) != null) {
                Object answer = getValue(avniEntity, obsField);
                if(answer instanceof List<?>) {
                    //Convert string array to single string
                    List<String> answers = (List<String>) answer;
                    answer = String.join(",", answers);
                }
                observationHolder.put(mapping.getIntSystemValue(), answer);
            }
        }
    }

    private Object getValue(AvniBaseContract avniEntity, String obsField) {
        Object returnedValue = null;
        returnedValue = avniEntity.get(obsField);
        if (returnedValue == null) {
            returnedValue = avniEntity.getObservation(obsField);
        }
        return returnedValue;
    }

    private RestClientResponseException getRestClientResponseException(HttpHeaders headers, HttpStatus statusCode, String message) {
        return switch (statusCode.series()) {
            case CLIENT_ERROR -> HttpClientErrorException.create(message, statusCode, null, headers, null, null);
            case SERVER_ERROR -> HttpServerErrorException.create(message, statusCode, null, headers, null, null);
            default -> new UnknownHttpStatusCodeException(message, statusCode.value(), null, headers, null, null);
        };
    }

    private HttpEntity<Map<String, List>> getDeleteEncounterHttpRequestEntity(GeneralEncounter encounter) {
        Map<String, List> deleteRequest = Map.of(DELETION_RECORD_ID, new ArrayList(), DELETION_SOURCE_ID, Arrays.asList(encounter.getUuid()));
        HttpEntity<Map<String, List>> requestEntity = new HttpEntity<>(deleteRequest);
        return requestEntity;
    }


    public abstract HashMap<String, Object>[] fetchEvents();

    public abstract <T extends AmritBaseResponse> T createEvent(AvniBaseContract subject, AvniBaseContract avniEntity, Class<T> returnType);

    public boolean wasEventCreatedSuccessfully(HashMap<String, Object>[] response) {
        return (response != null && response[0].get("errorCode") == null);
    }

    public Object deleteEvent(String resourceType, GeneralEncounter encounter) {
        HttpEntity<Map<String, List>> requestEntity = getDeleteEncounterHttpRequestEntity(encounter);
        return deleteSingleEntity(resourceType, requestEntity);
    }

    public AmritFetchIdentityResponse getAmritId(String individualUUID) {
        return getSingleEntityResponse(amritApplicationConfig.getIdentityApiPrefix() + FETCH_AMRIT_ID_RESOURCE_PATH,
                new HttpEntity<>(new String[] {individualUUID}), AmritFetchIdentityResponse.class);
    }

}
