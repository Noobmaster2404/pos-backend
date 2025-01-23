package com.increff.server.entity;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
//In this case, the BaseEntity class contains lifecycle methods, and any class that extends BaseEntity will automatically inherit these lifecycle methods. 
//The lifecycle methods will be triggered when an entity instance (like Person) is persisted or updated.
//Hence we dont need EntityListeners
public class Client extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, 
                    generator = "client_generator")
    @TableGenerator(
        name = "client_generator",
        table = "sequence_table",
        pkColumnName = "seq_name",
        valueColumnName = "seq_value",
        pkColumnValue = "client_seq",
        initialValue = 1,
        allocationSize = 50,
        //allocationSize is the number of IDs to be allocated in one go
        //They are kept in cache and are allocated in chunks
        //This is better than identity because it doesn't need to query the database for the last ID on every insert
        //There is only one issue, if the system crashes, the IDs are lost
        //But thats a tradeoff for better performance
        //Hence its better than the identity strategy
        schema = "pos"
    )
    
    private Integer clientId;
    @Column(length = 256, nullable = false)
    private String clientName;
    @Column(length = 10, nullable = false)
    private String phone;
    @Column(length = 256, nullable = false)
    private String email;
    @Column(nullable = false)
    private Boolean enabled = true;
}
