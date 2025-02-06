package com.increff.commons.model;

import lombok.Getter;
import lombok.Setter;
import java.time.ZonedDateTime;

@Getter
@Setter
public class DailySalesData {
    private ZonedDateTime date;
    private Integer invoicedOrderCount;
    private Integer totalItems;
    private Double totalRevenue;
} 