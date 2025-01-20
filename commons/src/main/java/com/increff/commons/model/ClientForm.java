package com.increff.commons.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientForm {

    private String name;
    private String phone;
    private String email;
    private Boolean enabled = true; 
}
