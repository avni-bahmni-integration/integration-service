package org.avni_integration_service.avni.client;

import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import org.avni_integration_service.avni.auth.AuthenticationHelper;
import org.avni_integration_service.avni.domain.auth.IdpDetailsResponse;
import org.avni_integration_service.avni.domain.auth.KeycloakDetails;
import org.avni_integration_service.avni.domain.auth.KeycloakResponse;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;

/**
 * This class is to allow for different types of connection with Avni.
 * Different environment & different users.
 */
public class AvniSession {
    private final String avniApiUrl;
    private final String avniImplUser;
    private final String avniImplUserPassword;
    private final boolean authWithAvni;

    private AuthenticationResultType authenticationResultType;
    private final IdpType idpType;
    private KeycloakResponse keycloakResponse;

    public AvniSession(String avniApiUrl, String avniImplUser, String avniImplUserPassword, boolean authWithAvni, IdpType idpType) {
        this.idpType = idpType;
        if (authWithAvni) {
            if (!StringUtils.hasText(avniApiUrl)) throw new IllegalArgumentException(String.format("Invalid API URL: %s", avniApiUrl));
            if (!StringUtils.hasText(avniImplUser)) throw new IllegalArgumentException(String.format("Invalid Impl User: %s", avniImplUser));
            if (!StringUtils.hasText(avniImplUserPassword)) throw new IllegalArgumentException(String.format("Invalid Impl User Password: %s", avniImplUserPassword));
        }

        this.avniApiUrl = avniApiUrl;
        this.avniImplUser = avniImplUser;
        this.avniImplUserPassword = avniImplUserPassword;
        this.authWithAvni = authWithAvni;
    }

    public AvniSession(String avniApiUrl, String avniImplUser, String avniImplUserPassword, boolean authWithAvni) {
        this(avniApiUrl, avniImplUser, avniImplUserPassword, authWithAvni, IdpType.Cognito);
    }

    // couldn't get refresh token to work hence clearing auth information when token expires so that a new token is taken
    void clearAuthInformation() {
        authenticationResultType = null;
        keycloakResponse = null;
    }

    public Boolean getAuthWithAvni() {
        return authWithAvni;
    }

    public String getIdToken() {
        if (authenticationResultType != null && authenticationResultType.getIdToken() != null && !authenticationResultType.getIdToken().isEmpty()) {
            return authenticationResultType.getIdToken();
        } else if (keycloakResponse != null)
            return keycloakResponse.getIdToken();
        return null;
    }

    public String fetchIdToken(IdpDetailsResponse idpDetailsResponse) {
        if (idpType.equals(IdpType.Cognito)) {
            AuthenticationHelper helper = new AuthenticationHelper(idpDetailsResponse.getCognito().getPoolId(), idpDetailsResponse.getCognito().getClientId());
            authenticationResultType = helper.performSRPAuthentication(avniImplUser, avniImplUserPassword);
            return authenticationResultType.getIdToken();
        } else {
            KeycloakDetails keycloak = idpDetailsResponse.getKeycloak();
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("client_id", keycloak.getClientId());
            map.add("grant_type", "password");
            map.add("scope", "openid");
            map.add("username", avniImplUser);
            map.add("password", avniImplUserPassword);

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

            ResponseEntity<KeycloakResponse> responseEntity =
                    restTemplate.exchange(String.format("%s/realms/%s/protocol/openid-connect/token", keycloak.getAuthServerUrl(), keycloak.getRealm()),
                            HttpMethod.POST,
                            entity,
                            KeycloakResponse.class);
            keycloakResponse = responseEntity.getBody();
            return keycloakResponse.getIdToken();
        }
    }

    public String getUri(String url, HashMap<String, String> queryParams) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl(url));
        for (var entry : queryParams.entrySet()) {
            builder.queryParam(entry.getKey(), entry.getValue());
        }
        URI uri = builder.build().toUri();
        return uri.toString();
    }

    public String apiUrl(String url) {
        return String.format("%s%s", avniApiUrl, url);
    }

    String getAvniImplUser() {
        return avniImplUser;
    }
}
