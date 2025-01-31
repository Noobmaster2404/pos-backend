package com.increff.server.entity;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
    name = "order_items",
    uniqueConstraints = @UniqueConstraint(columnNames = {"id"})
)
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "order_item_generator")
    @TableGenerator(
        name = "order_item_generator",
        table = "sequence_table",
        pkColumnName = "seq_name",
        valueColumnName = "seq_value",
        pkColumnValue = "order_item_seq",
        initialValue = 1,
        allocationSize = 50,
        schema = "pos"
    )
    @Column(name = "id")
    private Integer orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double sellingPrice;
} 
