package com.increff.commons.model;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Email;

@Getter
@Setter
public class ClientForm {

    @NotBlank(message = "Client name cannot be empty")
    private String clientName;
    //@NotBlank = not null, not empty, and not only whitespace
    
    @NotBlank(message = "Client phone cannot be empty")
    @Pattern(regexp = "\\d{10}", message = "Phone must be exactly 10 digits")
    private String clientPhone;
    
    @NotBlank(message = "Client email cannot be empty")
    @Email(message = "Invalid email format")
    private String clientEmail;
    
    private Boolean clientEnabled = true; 
}
