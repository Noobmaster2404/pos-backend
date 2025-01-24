package com.increff.commons.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemData {
    private Integer orderItemId;
    private Integer productId;
    private String productName;
    private String barcode;
    private Integer quantity;
    private Double sellingPrice;
    private Double itemTotal;
} 