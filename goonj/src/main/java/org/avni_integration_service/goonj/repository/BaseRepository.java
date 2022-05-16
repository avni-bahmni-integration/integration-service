package org.avni_integration_service.goonj.repository;

import org.avni_integration_service.goonj.domain.AuthResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

public class BaseRepository {
    HttpEntity<Object> getHeaders(AuthResponse authResponse) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+authResponse.getAccessToken());
        return new HttpEntity<>(headers);
    }
}
