package org.avni_integration_service.amrit.dto;

import java.util.List;

public class AmritFetchIdentityResponse extends AmritBaseResponse {

    public List<String> getIds() {
        return (List<String>) getData();
    }
}
