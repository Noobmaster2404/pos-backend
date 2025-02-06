package com.increff.commons.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryData {
    private Integer productId;
    private Integer inventoryId;
    private String barcode;
    private Integer quantity;
}
