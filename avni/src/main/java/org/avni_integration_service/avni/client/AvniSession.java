package org.avni_integration_service.avni.client;

import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import org.avni_integration_service.avni.auth.AuthenticationHelper;
import org.avni_integration_service.avni.domain.CognitoDetailsResponse;
import org.springframework.util.StringUtils;
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
    private AuthenticationHelper helper;

    public AvniSession(String avniApiUrl, String avniImplUser, String avniImplUserPassword, boolean authWithAvni) {
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

    void refreshToken() {
        authenticationResultType = helper.refresh(authenticationResultType.getRefreshToken(), authenticationResultType.getIdToken());
    }

    // couldn't get refresh token to work hence clearing auth information when token expires so that a new token is taken
    void clearAuthInformation() {
        authenticationResultType = null;
    }

    public Boolean getAuthWithAvni() {
        return authWithAvni;
    }

    public String getIdToken() {
        if (authenticationResultType != null && authenticationResultType.getIdToken() != null && !authenticationResultType.getIdToken().isEmpty()) {
            return authenticationResultType.getIdToken();
        }
        return null;
    }

    public String fetchIdToken(CognitoDetailsResponse cognitoDetails) {
        helper = new AuthenticationHelper(cognitoDetails.getPoolId(), cognitoDetails.getClientId());
        authenticationResultType = helper.performSRPAuthentication(avniImplUser, avniImplUserPassword);
        return authenticationResultType.getIdToken();
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
