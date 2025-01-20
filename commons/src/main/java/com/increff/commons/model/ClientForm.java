package com.increff.commons.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientForm {

    private String name;
    private String contact;
    private Boolean enabled = true; 
}
