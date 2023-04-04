package org.avni_integration_service.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.log4j.Logger;
import org.avni_integration_service.dto.CallDTO;
import org.avni_integration_service.dto.CallDetailsDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Component
public class ExotelRepository {

    private static final Logger logger = Logger.getLogger(ExotelRepository.class);
    private final RestTemplate powerRestTemplate;

    public ExotelRepository(@Qualifier("PowerRestTemplate") RestTemplate restTemplate) {
        this.powerRestTemplate = restTemplate;
    }

    public CallDetailsDTO getCallDetailsFromURI(URI uri, HttpEntity<?> requestEntity) {
        ResponseEntity<CallDetailsDTO> responseEntity = powerRestTemplate.exchange(uri, HttpMethod.GET, requestEntity, CallDetailsDTO.class);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        }
        logger.error(String.format("Failed to fetch data for resource Call Details, response status code is %s", responseEntity.getStatusCode()));
        throw new HttpServerErrorException(responseEntity.getStatusCode());
    }

    public CallDTO getSingleCallDetails(URI uri, HttpEntity<?> requestEntity) {
        ResponseEntity<CallDTO> responseEntity = powerRestTemplate.exchange(uri, HttpMethod.GET, requestEntity, CallDTO.class);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        }
        logger.error(String.format("Failed to fetch data for the call, response status code is %s", responseEntity.getStatusCode()));
        throw new HttpServerErrorException(responseEntity.getStatusCode());
    }

}
