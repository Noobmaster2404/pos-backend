package com.increff.commons.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemForm {
    @NotNull(message = "Barcode cannot be null")
    private String barcode;

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 1000000, message = "Quantity cannot exceed 1,000,000")
    private Integer quantity;

    @NotNull(message = "Selling price cannot be null")
    @Min(value = 0, message = "Selling price cannot be negative")
    private Double sellingPrice;
} 