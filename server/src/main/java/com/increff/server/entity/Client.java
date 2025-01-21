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
    //fetches last id everytime which makes it slow
   
    // @GeneratedValue(strategy = GenerationType.TABLE, 
    //                 generator = "client_generator")
    // @TableGenerator(
    //     name = "client_generator",
    //     table = "sequence_table",
    //     pkColumnName = "seq_name",
    //     valueColumnName = "seq_value",
    //     pkColumnValue = "client_seq",
    //     initialValue = 1,
    //     allocationSize = 1
    // )
    private int id;
    @Column(length = 10, nullable = false)
    private String phone;
    @Column(length = 256, nullable = false)
    private String email;
    @Column(length = 256, nullable = false)
    private String name;
    @Column(nullable = false)
    private Boolean enabled = true;
}
