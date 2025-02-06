package com.increff.server.entity;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
    name = "inventory",
    uniqueConstraints = @UniqueConstraint(columnNames = {"product_id"})
)
public class Inventory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "inventory_generator")
    private Integer inventoryId;
    //TODO: exlplain analyze ... for optimizer

    @OneToOne
    @JoinColumn(name="product_id", nullable = false)
    //Use name here explicitly because when using foreign keys, hibernate has some wierd naming strategy
    //like product_product_id
    private Product product;

    @Column(nullable = false)
    private Integer quantity;
}
