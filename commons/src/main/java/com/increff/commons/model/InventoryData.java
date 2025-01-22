package com.increff.commons.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryData {
    private Integer inventoryId;
    private Integer productId;
    private String productBarcode;
    private Integer quantity;
}
