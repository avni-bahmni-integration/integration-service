package org.avni_integration_service.amrit.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "data",
        "statusCode",
        "errorMessage",
        "status"
})
@Generated("jsonschema2pojo")
public class AmritBaseResponse {

    @JsonProperty("data")
    private Object data = new Object();
    @JsonProperty("statusCode")
    private long statusCode;
    @JsonProperty("errorMessage")
    private String errorMessage;
    @JsonProperty("status")
    private String status;

    /**
     * No args constructor for use in serialization
     */
    public AmritBaseResponse() {
    }

    /**
     * @param data
     * @param errorMessage
     * @param statusCode
     * @param status
     */
    public AmritBaseResponse(Object data, long statusCode, String errorMessage, String status) {
        super();
        this.data = data;
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
        this.status = status;
    }

    @JsonProperty("data")
    public Object getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(Object data) {
        this.data = data;
    }

    @JsonProperty("statusCode")
    public long getStatusCode() {
        return statusCode;
    }

    @JsonProperty("statusCode")
    public void setStatusCode(long statusCode) {
        this.statusCode = statusCode;
    }

    @JsonProperty("errorMessage")
    public String getErrorMessage() {
        return errorMessage;
    }

    @JsonProperty("errorMessage")
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

}