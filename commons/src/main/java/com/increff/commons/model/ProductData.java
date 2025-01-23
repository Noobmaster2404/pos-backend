package com.increff.commons.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductData extends ProductForm {
    private Integer productId;
    private Double mrp;
    private String barcode;
    private String productName;
    private Integer clientId;
    private String imagePath;

    private String clientName;
    private String quantity;
}
