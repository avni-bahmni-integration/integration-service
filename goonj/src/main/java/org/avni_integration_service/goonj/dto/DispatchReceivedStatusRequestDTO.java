
package org.avni_integration_service.goonj.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "DispatchReceivedStatus"
})
public class DispatchReceivedStatusRequestDTO {

    @JsonProperty("DispatchReceivedStatus")
    private List<DispatchReceivedstatus> dispatchReceivedStatus = new ArrayList<DispatchReceivedstatus>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public DispatchReceivedStatusRequestDTO() {
    }

    /**
     * 
     * @param dispatchReceivedStatus
     */
    public DispatchReceivedStatusRequestDTO(List<DispatchReceivedstatus> dispatchReceivedStatus) {
        super();
        this.dispatchReceivedStatus = dispatchReceivedStatus;
    }

    @JsonProperty("DispatchReceivedStatus")
    public List<DispatchReceivedstatus> getDispatchReceivedStatus() {
        return dispatchReceivedStatus;
    }

    @JsonProperty("DispatchReceivedStatus")
    public void setDispatchReceivedStatus(List<DispatchReceivedstatus> dispatchReceivedStatus) {
        this.dispatchReceivedStatus = dispatchReceivedStatus;
    }

}
