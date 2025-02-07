package com.increff.commons.model;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesReportForm {
    @NotNull(message = "Start date cannot be null")
    @PastOrPresent(message = "Start date must not be in future")
    private ZonedDateTime startDate;
    
    @NotNull(message = "End date cannot be null")
    @PastOrPresent(message = "End date must not be in future")
    private ZonedDateTime endDate;
    
    @Size(max = 255, message = "Client name cannot exceed 255 characters")
    private String clientId; // Exact client name after selection from search
} 