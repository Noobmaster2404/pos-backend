package com.increff.commons.model;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Email;

@Getter
@Setter
public class ClientForm {

    @NotEmpty(message = "Name cannot be empty")
    private String name;
    //@NotEmpty contains a null check by default
    
    @NotEmpty(message = "Phone cannot be empty")
    @Pattern(regexp = "\\d{10}", message = "Phone must be exactly 10 digits")
    private String phone;
    
    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;
    
    private Boolean enabled = true; 
}
