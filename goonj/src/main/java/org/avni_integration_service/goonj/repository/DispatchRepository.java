package org.avni_integration_service.goonj.repository;

import org.avni_integration_service.goonj.config.GoonjConfig;
import org.avni_integration_service.goonj.domain.AuthResponse;
import org.avni_integration_service.goonj.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;

@Component
public class DispatchRepository extends BaseRepository {
    @Autowired
    public DispatchRepository(RestTemplate restTemplate, GoonjConfig goonjConfig) {
        super(restTemplate, goonjConfig);
    }

    public HashMap<String, Object>[] getDispatches(AuthResponse authResponse, LocalDateTime dateTime) {
        return super.getResponse(authResponse, dateTime, "DispatchService/getDispatches");
    }
}
