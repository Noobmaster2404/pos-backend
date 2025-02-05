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
    //TODO: remove barcode from here and use joins instead
    //exlplain analyze ... for optimizer

    @OneToOne
    @JoinColumn(nullable = false)
    private Product product;

    @Column(nullable = false)
    private String barcode;
    //TODO:remove barcode

    @Column(nullable = false)
    private Integer quantity;
}
