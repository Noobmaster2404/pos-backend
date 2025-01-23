package com.increff.commons.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientData {
    private Integer clientId;
    private String clientName;
    private String phone;
    private String email;
    private Boolean enabled;
}

