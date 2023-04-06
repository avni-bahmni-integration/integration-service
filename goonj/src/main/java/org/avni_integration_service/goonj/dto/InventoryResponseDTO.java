package org.avni_integration_service.goonj.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "ImplementationInventory"
})
public class InventoryResponseDTO {

    @JsonProperty("ImplementationInventory")
    private HashMap<String, Object>[] inventoryItemsDTOS = null;
    /**
     * No args constructor for use in serialization
     *
     */
    public InventoryResponseDTO() {
    }
    /**
     *
     * @param inventoryItemsDTOS
     */
    public InventoryResponseDTO(HashMap<String, Object>[] inventoryItemsDTOS) {
        super();
        this.inventoryItemsDTOS = inventoryItemsDTOS;
    }
    @JsonProperty("ImplementationInventory")
    public HashMap<String, Object>[] getInventoryItemsDTOS() {
        return inventoryItemsDTOS;
    }
    @JsonProperty("ImplementationInventory")
    public void setInventoryItemsDTOS(HashMap<String, Object>[] inventoryItemsDTOS) {
        this.inventoryItemsDTOS = inventoryItemsDTOS;
    }
}