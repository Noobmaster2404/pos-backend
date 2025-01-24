package com.increff.server.entity;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
    name = "clients"
)
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
        schema = "pos"
    )
    @Column(name = "client_id")
    private Integer clientId;
    @Column(name = "client_name", length = 256, nullable = false, unique = true)
    private String clientName;
    @Column(length = 10, nullable = false)
    private String phone;
    @Column(length = 256, nullable = false)
    private String email;
    @Column(nullable = false)
    private Boolean enabled = true;
}
