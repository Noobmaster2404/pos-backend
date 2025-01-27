package com.increff.commons.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductForm {
    
    @NotBlank(message = "Product barcode cannot be blank")
    @Size(max = 255, message = "Product barcode cannot exceed 255 characters")
    private String barcode;

    @NotBlank(message = "Product name cannot be blank")
    @Size(max = 255, message = "Product name cannot exceed 255 characters")
    private String productName;
    
    @NotBlank(message = "Client name cannot be blank")
    @Size(max = 255, message = "Client name cannot exceed 255 characters")
    private String clientName;
    
    @Pattern(regexp = "^[\\w-]+\\.(jpg|jpeg|png)$", 
            message = "Image path must be a valid filename with extension jpg, jpeg, or png")
    @Size(max = 255, message = "Image path cannot exceed 255 characters")
    private String imagePath;
    
    @NotNull(message = "Product MRP cannot be null")
    @Min(value = 0, message = "Product MRP must be positive")
    private Double mrp;

}
