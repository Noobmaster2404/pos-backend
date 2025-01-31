package com.increff.server.entity;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(
    name = "pos_day_sales",
    uniqueConstraints = @UniqueConstraint(columnNames = {"date"})
)
public class DailySales extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "daily_sales_generator")
    @TableGenerator(
        name = "daily_sales_generator",
        table = "sequence_table",
        pkColumnName = "seq_name",
        valueColumnName = "seq_value",
        pkColumnValue = "daily_sales_seq",
        initialValue = 1,
        allocationSize = 1,
        schema = "pos"
    )
    private Integer id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Integer invoicedOrders;

    @Column(nullable = false)
    private Integer totalItems;

    @Column(nullable = false)
    private Double totalRevenue;
} 