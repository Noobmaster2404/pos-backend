package com.increff.commons.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientData {
    //TODO: use integer instead of int
    private int id;
    private String name;
    private String phone;
    private String email;
    private Boolean enabled;
}

