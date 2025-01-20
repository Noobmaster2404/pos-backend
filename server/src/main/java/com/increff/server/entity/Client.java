package com.increff.server.entity;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Client extends BaseEntity {

    //TODO add table generation method instead of identity
    //TODO use box type
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto increment
    private int id;
    private String phone;
    private String email;
    private String name;
    // TODO to add min length and mx length validations here only
    
    @Column(nullable = false)
    private Boolean enabled = true;
    //TODO: Primitive boolean supports isEnabled but Object doesn’t
}
