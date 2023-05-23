package org.avni_integration_service.goonj.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"ImplementationInventory", "deletedImplementationInventory"})
public class InventoryResponseDTO {

    @JsonProperty("ImplementationInventory")
    private HashMap<String, Object>[] inventoryItemsDTOS = null;
    @JsonProperty("deletedImplementationInventory")
    private Object[] deletedItemsDTOS = null;

    /**
     * No args constructor for use in serialization
     */
    public InventoryResponseDTO() {
    }

    /**
     * @param inventoryItemsDTOS
     * @param deletedItemsDTOS
     */
    public InventoryResponseDTO(HashMap<String, Object>[] inventoryItemsDTOS, Object[] deletedItemsDTOS) {
        super();
        this.inventoryItemsDTOS = inventoryItemsDTOS;
        this.deletedItemsDTOS = deletedItemsDTOS;
    }

    @JsonProperty("ImplementationInventory")
    public HashMap<String, Object>[] getInventoryItemsDTOS() {
        return inventoryItemsDTOS;
    }

    @JsonProperty("ImplementationInventory")
    public void setInventoryItemsDTOS(HashMap<String, Object>[] inventoryItemsDTOS) {
        this.inventoryItemsDTOS = inventoryItemsDTOS;
    }

    @JsonProperty("deletedImplementationInventory")
    public Object[] getDeletedItemsDTOS() {
        return deletedItemsDTOS;
    }

    @JsonProperty("deletedImplementationInventory")
    public void setDeletedItemsDTOS(Object[] deletedItemsDTOS) {
        this.deletedItemsDTOS = deletedItemsDTOS;
    }
}