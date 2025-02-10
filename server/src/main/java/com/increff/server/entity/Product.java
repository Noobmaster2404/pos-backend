package com.increff.server.entity;

import javax.persistence.*;
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
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "product_generator")
    private Integer productId;

    @Column(length = 255, nullable = false)
    private String barcode;

    @Column(length = 255, nullable = false)
    private String productName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    //@JoinColumn specifies the foreign key column in the database and is specified in child table
    private Client client;
    //While in the database, this is stored as a foreign key (client_id)
    //In the Java code, using the actual Client object instead of just clientId provides several benefits
    //You can directly access client properties through the Product object (e.g., product.getClient().getName())
    //JPA automatically handles the database joins when you need client information

    @Column(length = 1000)
    private String imagePath;

    @Column(nullable = false)
    private Double mrp;

}
