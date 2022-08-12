package org.avni_integration_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

public class CallDTO {
    @JsonProperty("Call")
    private HashMap<String, Object> call;

    public CallDTO() {
    }

    public HashMap<String, Object> getCall() {
        return call;
    }

    public void setCall(HashMap<String, Object> call) {
        this.call = call;
    }

}
