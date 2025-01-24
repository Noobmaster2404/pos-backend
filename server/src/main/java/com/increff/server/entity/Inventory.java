package com.increff.server.entity;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
    name = "inventory"
)
public class Inventory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, 
                    generator = "inventory_generator")
    @TableGenerator(
        name = "inventory_generator",
        table = "sequence_table",
        pkColumnName = "seq_name",
        valueColumnName = "seq_value",
        pkColumnValue = "inventory_seq",
        initialValue = 1,
        allocationSize = 50,
        schema = "pos"
    )
    @Column(name = "inventory_id")
    private Integer inventoryId;

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @Column(nullable = false)
    private String barcode;
    //include it in the product object above

    @Column(nullable = false)
    private Integer quantity;
}
