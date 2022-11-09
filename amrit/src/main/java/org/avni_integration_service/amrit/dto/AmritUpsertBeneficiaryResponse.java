package org.avni_integration_service.amrit.dto;

import java.util.HashMap;

public class AmritUpsertBeneficiaryResponse extends AmritBaseResponse {

    public HashMap<String, Object> getResponse() {
        return (HashMap<String, Object>) getData();
    }
}