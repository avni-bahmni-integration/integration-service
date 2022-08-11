package org.avni_integration_service.service;

import org.apache.log4j.Logger;
import org.avni_integration_service.PowerEntityType;
import org.avni_integration_service.config.PowerConfig;
import org.avni_integration_service.dto.CallDTO;
import org.avni_integration_service.dto.CallDetailsDTO;
import org.avni_integration_service.integration_data.repository.IntegratingEntityStatusRepository;
import org.avni_integration_service.repository.ExotelRepository;
import org.avni_integration_service.util.DateTimeUtil;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Service
public class CallDetailsService {
    private static final Logger logger = Logger.getLogger(CallDetailsService.class);
    private final IntegratingEntityStatusRepository integratingEntityStatusRepository;
    private final PowerConfig powerConfig;
    private final ExotelRepository exotelRepository;

    public CallDetailsService(IntegratingEntityStatusRepository integratingEntityStatusRepository,
                              PowerConfig powerConfig, ExotelRepository exotelRepository) {
        this.integratingEntityStatusRepository = integratingEntityStatusRepository;
        this.powerConfig = powerConfig;
        this.exotelRepository = exotelRepository;
    }

    public CallDetailsDTO fetchBulkCallDetails() {
        URI uri = getCallDetailsURI();
        logger.info("Fetching the bulk call details");
        return exotelRepository.getCallDetailsFromURI(uri, getRequestEntity());
    }

    public CallDetailsDTO fetchUsingNextPageURI(String nextPageURI) {
        logger.info("Fetching the next page");
        try {
            URI uri = new URI(String.format("https://%s%s", powerConfig.getExotelSubdomain(), nextPageURI));
            return exotelRepository.getCallDetailsFromURI(uri, getRequestEntity());
        } catch (URISyntaxException e) {
            throw new RuntimeException("error for uri:" + nextPageURI, e);
        }
    }

    public CallDTO fetchCallBySID(String sid) {
        try {
            URI uri = new URI(powerConfig.getCallDetailsAPI(sid));
            logger.info(String.format("Fetching call details by sid %s", sid));
            return exotelRepository.getSingleCallDetails(uri, getRequestEntity());
        } catch (URISyntaxException e) {
            throw new RuntimeException("error while creating call fetch uri:" + e);
        }
    }

    private URI getCallDetailsURI() {
        Date readUptoDateTime = integratingEntityStatusRepository.findByEntityType(PowerEntityType.CALL_DETAILS.getDbName()).getReadUptoDateTime();
        String fromDateQuery = URLEncoder.encode(String.format("gte:%s;", DateTimeUtil.formatDateTime(readUptoDateTime)), StandardCharsets.UTF_8);
        String toDateQuery = URLEncoder.encode(String.format("lte:%s", DateTimeUtil.formatDateTime(new Date())), StandardCharsets.UTF_8);
        try {
            return new URI(String.format("%s?DateCreated=%s&SortBy=%s&PageSize=100",
                    powerConfig.getCallDetailsAPI(null),
                    String.format("%s%s", fromDateQuery, toDateQuery),
                    URLEncoder.encode("DateCreated:asc", StandardCharsets.UTF_8)
            ));
        } catch (URISyntaxException e) {
            throw new RuntimeException("error while creating fetch call details uri:" + e);
        }
    }

    private HttpEntity<String> getRequestEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return new HttpEntity<String>(headers);
    }
}
