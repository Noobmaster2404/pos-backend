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
@Table (name = "orders")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "order_generator")
    private Integer orderId;

    @Column(nullable = false)
    private ZonedDateTime orderTime;

    @Column(nullable = false)
    private Double orderTotal;

    @Column(length = 1000)
    private String invoicePath;

    @Column(nullable = false)
    private Boolean invoiceGenerated = false;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    //default fetch type is lazy for OneToMany
    //mappedBy helps hibernate to know that the foreign key is in the OrderItem table
    //hence avoiding the need to create a join table with orderId and orderItemId
    //cascade type is all because we want to delete the order items when the order is deleted 
    //and insert the order items when the order is created
    
    private List<OrderItem> orderItems = new ArrayList<>();
    //The above is not in workbench because when it is mappedBy order, hibernate knows that order has the FK
    //In one to many, current side is treated as parent and other as child
} 