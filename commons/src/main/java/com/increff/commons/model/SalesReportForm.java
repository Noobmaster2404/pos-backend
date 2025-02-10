package com.increff.commons.model;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesReportForm {
    @NotNull(message = "Start date cannot be null")
    @PastOrPresent(message = "Start date must not be in future")
    private LocalDate startDate;
    
    @NotNull(message = "End date cannot be null")
    @PastOrPresent(message = "End date must not be in future")
    private LocalDate endDate;
    
    private Integer clientId;
} 