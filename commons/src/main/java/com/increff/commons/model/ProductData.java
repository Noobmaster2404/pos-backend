package com.increff.commons.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductData extends ProductForm {
    private Integer productId;
    private Double productMrp;
    private String productBarcode;
    private String productName;
    private Integer clientId;
    private String productImagePath;

    private String clientName;
    private String quantity;
}
