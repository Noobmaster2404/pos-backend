package com.increff.server.entity;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
    name = "products",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id"}),
        @UniqueConstraint(columnNames = {"barcode"})
    }
)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, 
                    generator = "product_generator")
    @TableGenerator(
        name = "product_generator",
        table = "sequence_table",
        pkColumnName = "seq_name",
        valueColumnName = "seq_value",
        pkColumnValue = "product_seq",
        initialValue = 1,
        allocationSize = 50,
        schema = "pos"
    )
    @Column(name = "id")
    private Integer productId;

    @Column(name = "barcode", length = 255, nullable = false, unique = true)
    private String barcode;

    @Column(length = 255, nullable = false)
    private String productName;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    //check client_id
    private Client client;
    //While in the database, this is stored as a foreign key (client_id)
    //In the Java code, using the actual Client object instead of just clientId provides several benefits
    //You can directly access client properties through the Product object (e.g., product.getClient().getName())
    //PA automatically handles the database joins when you need client information

    @Column(length = 1000)
    private String imagePath;

    @Column(nullable = false)
    private Double mrp;

}
