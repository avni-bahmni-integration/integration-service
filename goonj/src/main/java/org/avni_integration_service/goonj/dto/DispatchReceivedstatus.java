
package org.avni_integration_service.goonj.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "SourceId",
    "DispatchStatusId",
    "ReceivedDate",
    "DispatchReceivedStatusLineItems"
})
public class DispatchReceivedstatus {

    @JsonProperty("SourceId")
    private String sourceId;
    @JsonProperty("DispatchStatusId")
    private String dispatchStatusId;
    @JsonProperty("ReceivedDate")
    private String receivedDate;
    @JsonProperty("DispatchReceivedStatusLineItems")
    private List<DispatchReceivedStatusLineItem> dispatchReceivedStatusLineItems = new ArrayList<DispatchReceivedStatusLineItem>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public DispatchReceivedstatus() {
    }

    /**
     * 
     * @param sourceId
     * @param dispatchStatusId
     * @param dispatchReceivedStatusLineItems
     * @param receivedDate
     */
    public DispatchReceivedstatus(String sourceId, String dispatchStatusId, String receivedDate, List<DispatchReceivedStatusLineItem> dispatchReceivedStatusLineItems) {
        super();
        this.sourceId = sourceId;
        this.dispatchStatusId = dispatchStatusId;
        this.receivedDate = receivedDate;
        this.dispatchReceivedStatusLineItems = dispatchReceivedStatusLineItems;
    }

    @JsonProperty("SourceId")
    public String getSourceId() {
        return sourceId;
    }

    @JsonProperty("SourceId")
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    @JsonProperty("DispatchStatusId")
    public String getDispatchStatusId() {
        return dispatchStatusId;
    }

    @JsonProperty("DispatchStatusId")
    public void setDispatchStatusId(String dispatchStatusId) {
        this.dispatchStatusId = dispatchStatusId;
    }

    @JsonProperty("ReceivedDate")
    public String getReceivedDate() {
        return receivedDate;
    }

    @JsonProperty("ReceivedDate")
    public void setReceivedDate(String receivedDate) {
        this.receivedDate = receivedDate;
    }

    @JsonProperty("DispatchReceivedStatusLineItems")
    public List<DispatchReceivedStatusLineItem> getDispatchReceivedStatusLineItems() {
        return dispatchReceivedStatusLineItems;
    }

    @JsonProperty("DispatchReceivedStatusLineItems")
    public void setDispatchReceivedStatusLineItems(List<DispatchReceivedStatusLineItem> dispatchReceivedStatusLineItems) {
        this.dispatchReceivedStatusLineItems = dispatchReceivedStatusLineItems;
    }

}
