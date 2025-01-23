package com.increff.commons.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryData {
    private Integer productId;
    private String barcode;
    private Integer quantity;
}
