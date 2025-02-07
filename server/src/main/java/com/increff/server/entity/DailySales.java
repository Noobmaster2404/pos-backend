package com.increff.server.entity;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@Table(
    name = "daily_sales",
    uniqueConstraints = @UniqueConstraint(columnNames = {"date"})
)
public class DailySales extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "daily_sales_generator")
    private Integer id;

    @Column(nullable = false)
    private ZonedDateTime date;

    @Column(nullable = false)
    private Integer invoicedOrdersCount;

    @Column(nullable = false)
    private Integer itemCount;

    @Column(nullable = false)
    private Double totalRevenue;
} 