package com.increff.server.entity;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.ArrayList;
@Entity
@Getter
@Setter
@Table(
    name = "orders",
    uniqueConstraints = @UniqueConstraint(columnNames = {"id"})
)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "order_generator")
    @TableGenerator(
        name = "order_generator",
        table = "sequence_table",
        pkColumnName = "seq_name",
        valueColumnName = "seq_value",
        pkColumnValue = "order_seq",
        initialValue = 1,
        allocationSize = 50,
        schema = "pos"
    )
    @Column(name = "id")
    private Integer orderId;

    @Column(nullable = false)
    private ZonedDateTime orderTime;

    @Column(nullable = false)
    private Double orderTotal;

    @Column(length = 1000)
    private String invoicePath;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();
} 