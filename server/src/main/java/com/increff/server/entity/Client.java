package com.increff.server.entity;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
    name = "clients",
    uniqueConstraints = @UniqueConstraint(columnNames = {"client_name"})
)

public class Client extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "client_generator")
    private Integer clientId;

    @Column(name = "client_name", length = 255, nullable = false)
    private String clientName;

    @Column(length = 10, nullable = false)
    private String phone;

    @Column(length = 255, nullable = false)
    private String email;

    @Column(nullable = false)
    private Boolean enabled = true;
    //Not adding @OneToMany here because dont need to access products of a client much (unidirectional relationship)
}
