package org.avni_integration_service.goonj.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "DispatchStatuses",
        "DeletedObjects"
})
public class DispatchesResponseDTO {

    @JsonProperty("DispatchStatuses")
    private HashMap<String, Object>[] dispatchStatusDTOS = null;
    @JsonProperty("DeletedObjects")
    private DispatchesDeletedObjectsDTO dispatchesDeletedObjectsDTO;

    /**
     * No args constructor for use in serialization
     *
     */
    public DispatchesResponseDTO() {
    }

    /**
     *
     * @param dispatchesDeletedObjectsDTO
     * @param dispatchStatusDTOS
     */
    public DispatchesResponseDTO(HashMap<String, Object>[] dispatchStatusDTOS, DispatchesDeletedObjectsDTO dispatchesDeletedObjectsDTO) {
        super();
        this.dispatchStatusDTOS = dispatchStatusDTOS;
        this.dispatchesDeletedObjectsDTO = dispatchesDeletedObjectsDTO;
    }

    @JsonProperty("DispatchStatuses")
    public HashMap<String, Object>[] getDispatchStatuses() {
        return dispatchStatusDTOS;
    }

    @JsonProperty("DispatchStatuses")
    public void setDispatchStatuses(HashMap<String, Object>[] dispatchStatusDTOS) {
        this.dispatchStatusDTOS = dispatchStatusDTOS;
    }

    @JsonProperty("DeletedObjects")
    public DispatchesDeletedObjectsDTO getDeletedObjects() {
        return dispatchesDeletedObjectsDTO;
    }

    @JsonProperty("DeletedObjects")
    public void setDeletedObjects(DispatchesDeletedObjectsDTO dispatchesDeletedObjectsDTO) {
        this.dispatchesDeletedObjectsDTO = dispatchesDeletedObjectsDTO;
    }

}