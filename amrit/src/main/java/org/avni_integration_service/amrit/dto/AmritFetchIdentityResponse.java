package org.avni_integration_service.amrit.dto;

import java.util.List;

public class AmritFetchIdentityResponse extends AmritBaseResponse {

    public List<Long> getIds() {
        return (List<Long>) getData();
    }
}
