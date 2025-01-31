package com.increff.commons.model;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class DailySalesData {
    private LocalDate date;
    private Integer invoicedOrderCount;
    private Integer totalItems;
    private Double totalRevenue;
} 