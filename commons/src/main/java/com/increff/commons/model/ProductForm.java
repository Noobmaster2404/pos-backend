package com.increff.commons.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import javax.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductForm {
    
    @NotBlank(message = "Product barcode cannot be blank")
    @Size(max = 255, message = "Product barcode cannot exceed 255 characters")
    private String productBarcode;

    @NotBlank(message = "Product name cannot be blank")
    @Size(max = 255, message = "Product name cannot exceed 255 characters")
    private String productName;
    
    @Positive(message = "Client ID must be positive")
    @NotNull(message = "Client ID cannot be null")
    private Integer clientId;
    
    @Size(max = 1000, message = "Product image path cannot exceed 1000 characters")
    private String productImagePath;
    
    @NotNull(message = "Product MRP cannot be null")
    @Min(value = 0, message = "Product MRP must be positive")
    private Double productMrp;

}
