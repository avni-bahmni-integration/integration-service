package org.avni_integration_service.goonj.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "DeletedDispatchStatusLineItems",
        "DeletedDispatchStatuses"
})
public class DispatchesDeletedObjectsDTO {
    @JsonProperty("DeletedDispatchStatusLineItems")
    private List<DeletedDispatchStatusLineItem> deletedDispatchStatusLineItems = null;
    @JsonProperty("DeletedDispatchStatuses")
    private List<String> deletedDispatchStatuses = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public DispatchesDeletedObjectsDTO() {
    }

    /**
     *
     * @param deletedDispatchStatusLineItems
     * @param deletedDispatchStatuses
     */
    public DispatchesDeletedObjectsDTO(List<DeletedDispatchStatusLineItem> deletedDispatchStatusLineItems, List<String> deletedDispatchStatuses) {
        super();
        this.deletedDispatchStatusLineItems = deletedDispatchStatusLineItems;
        this.deletedDispatchStatuses = deletedDispatchStatuses;
    }
    @JsonProperty("DeletedDispatchStatusLineItems")
    public List<DeletedDispatchStatusLineItem> getDeletedDispatchStatusLineItems() {
        return deletedDispatchStatusLineItems;
    }
    @JsonProperty("DeletedDispatchStatusLineItems")
    public void setDeletedDispatchStatusLineItems(List<DeletedDispatchStatusLineItem> deletedDispatchStatusLineItems) {
        this.deletedDispatchStatusLineItems = deletedDispatchStatusLineItems;
    }
    @JsonProperty("DeletedDispatchStatuses")
    public List<String> getDeletedDispatchStatuses() {
        return deletedDispatchStatuses;
    }
    @JsonProperty("DeletedDispatchStatuses")
    public void setDeletedDispatchStatuses(List<String> deletedDispatchStatuses) {
        this.deletedDispatchStatuses = deletedDispatchStatuses;
    }

}