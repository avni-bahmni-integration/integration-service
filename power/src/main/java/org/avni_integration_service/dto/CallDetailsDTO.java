package org.avni_integration_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.List;

public class CallDetailsDTO {
    @JsonProperty("Metadata")
    private HashMap<String, Object> metadata = null;

    @JsonProperty("Calls")
    private List<HashMap<String, Object>> calls = null;

    public CallDetailsDTO() {
    }

    @JsonProperty("Metadata")
    public void setMetadata(HashMap<String, Object> metadata) {
        this.metadata = metadata;
    }

    @JsonProperty("Calls")
    public void setCalls(List<HashMap<String, Object>> calls) {
        this.calls = calls;
    }

    @JsonProperty("Metadata")
    public HashMap<String, Object> getMetadata() {
        return metadata;
    }

    @JsonProperty("Calls")
    public List<HashMap<String, Object>> getCalls() {
        return calls;
    }
}
