package com.increff.commons.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryForm {
    
    @NotNull(message = "Product ID cannot be null")
    private Integer productId;
    
    @NotNull(message = "Quantity cannot be null")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;
}
