package org.avni_integration_service.service;

import org.apache.log4j.Logger;
import org.avni_integration_service.config.PowerEntityType;
import org.avni_integration_service.config.PowerConfig;
import org.avni_integration_service.dto.CallDTO;
import org.avni_integration_service.dto.CallDetailsDTO;
import org.avni_integration_service.integration_data.domain.IntegratingEntityStatus;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class CallDetailsService {
    public static final int DAYS_INTERVAL = 30;
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

    public CallDetailsDTO fetchBulkCallDetails(String phoneNumber) {
        URI uri = getCallDetailsURI(phoneNumber);
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

    private URI getCallDetailsURI(String phoneNumber) {
        IntegratingEntityStatus integratingEntityStatus = getIntegratingEntityStatus(phoneNumber);
        Date startDateTime = integratingEntityStatus.getReadUptoDateTime();
        Date endDateTime = DateTimeUtil.addTimeToJavaUtilDate(startDateTime, DAYS_INTERVAL, Calendar.DAY_OF_MONTH);
        //Important: DateCreated time range should not be more than 31 days or after currentDateTime
        Date currentDateTime = new Date();
        if(endDateTime.after(currentDateTime)) {
            endDateTime = currentDateTime;
        }
        String fromDateQuery = URLEncoder.encode(String.format("gte:%s;", DateTimeUtil.formatDateTime(startDateTime)), StandardCharsets.UTF_8);
        String toDateQuery = URLEncoder.encode(String.format("lte:%s", DateTimeUtil.formatDateTime(endDateTime)), StandardCharsets.UTF_8);
        try {
            return new URI(String.format("%s?DateCreated=%s&SortBy=%s&PageSize=100&To=%s.json",
                    powerConfig.getCallDetailsAPI(null),
                    String.format("%s%s", fromDateQuery, toDateQuery),
                    URLEncoder.encode("DateCreated:asc", StandardCharsets.UTF_8),
                    phoneNumber
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

    public IntegratingEntityStatus getIntegratingEntityStatus(String phoneNumber) {
        String entityType = String.format("%s::%s", PowerEntityType.CALL_DETAILS.getDbName(), phoneNumber);
        IntegratingEntityStatus integratingEntityStatus = integratingEntityStatusRepository.findByEntityType(entityType);
        if(integratingEntityStatus == null) {
            throw new RuntimeException("Unable to fetch integratingEntityStatus for phoneNumber: " +phoneNumber);
        }
        return integratingEntityStatus;
    }
}
