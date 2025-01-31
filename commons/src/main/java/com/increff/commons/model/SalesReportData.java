package com.increff.commons.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesReportData {
    private String barcode;
    private String productName;
    private String clientName;
    private Integer quantity;
    private Double revenue;
    private Double averageSellingPrice;
} 