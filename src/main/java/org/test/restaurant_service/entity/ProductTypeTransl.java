package org.test.restaurant_service.entity;

import lombok.*;

import javax.persistence.*;
import javax.persistence.Table;

@Entity
@Table(name = "product_type_translations", schema = "restaurant_service")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductTypeTransl {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "product_type_id", nullable = false, foreignKey = @ForeignKey(name = "fk_product_type_translation"))
    private ProductType productType;

    @Column(nullable = false, length = 10)
    private String languageCode;

    @Column(nullable = false, length = 128)
    private String name;
}
