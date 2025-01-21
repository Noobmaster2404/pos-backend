package com.increff.commons.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductForm {
    
    @NotBlank(message = "Barcode cannot be empty")
    private String barcode;
    
    @NotBlank(message = "Name cannot be empty")
    private String name;
    
    @NotNull(message = "Client ID cannot be null")
    private Integer clientId;
    
    private String imagePath;
    
    @NotNull(message = "MRP cannot be null")
    @Positive(message = "MRP must be positive")
    private Double mrp;

}
