package com.increff.commons.model;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderData {
    private Integer orderId;
    private ZonedDateTime orderTime;
    private Double orderTotal;
    private String invoicePath;
    private Boolean invoiceGenerated;
    private List<OrderItemData> orderItems;
} 