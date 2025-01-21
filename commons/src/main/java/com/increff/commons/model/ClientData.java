package com.increff.commons.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientData {
    private Integer clientId;
    private String clientName;
    private String clientPhone;
    private String clientEmail;
    private Boolean clientEnabled;
}

