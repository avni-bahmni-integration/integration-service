package org.avni_integration_service.goonj.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "DispatchStatusLineItemId",
        "DispatchStatusId"
})
public class DeletedDispatchStatusLineItem {

    @JsonProperty("DispatchStatusLineItemId")
    private String dispatchStatusLineItemId;
    @JsonProperty("DispatchStatusId")
    private Object dispatchStatusId;

    /**
     * No args constructor for use in serialization
     *
     */
    public DeletedDispatchStatusLineItem() {
    }

    /**
     *
     * @param dispatchStatusId
     * @param dispatchStatusLineItemId
     */
    public DeletedDispatchStatusLineItem(String dispatchStatusLineItemId, Object dispatchStatusId) {
        super();
        this.dispatchStatusLineItemId = dispatchStatusLineItemId;
        this.dispatchStatusId = dispatchStatusId;
    }

    @JsonProperty("DispatchStatusLineItemId")
    public String getDispatchStatusLineItemId() {
        return dispatchStatusLineItemId;
    }

    @JsonProperty("DispatchStatusLineItemId")
    public void setDispatchStatusLineItemId(String dispatchStatusLineItemId) {
        this.dispatchStatusLineItemId = dispatchStatusLineItemId;
    }

    @JsonProperty("DispatchStatusId")
    public Object getDispatchStatusId() {
        return dispatchStatusId;
    }

    @JsonProperty("DispatchStatusId")
    public void setDispatchStatusId(Object dispatchStatusId) {
        this.dispatchStatusId = dispatchStatusId;
    }

}