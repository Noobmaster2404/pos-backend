package com.increff.server.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
    name = "products",
    uniqueConstraints = @UniqueConstraint(columnNames = {"barcode"})
)
public class Product extends BaseEntity {

    @NotNull
    @Column(nullable = false)
    private String barcode;

    @NotNull
    @Column(nullable = false)
    private String name;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    private String imagePath;

    @NotNull
    @Column(nullable = false)
    private Double mrp;

}
