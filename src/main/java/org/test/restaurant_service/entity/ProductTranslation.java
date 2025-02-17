package org.test.restaurant_service.entity;

import lombok.*;

import javax.persistence.*;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "product_translations", schema = "restaurant_service",
        uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "language_code"}))
public class ProductTranslation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "fk_translation_product"))
    private Product product;

    @Column(name = "language_code", nullable = false, length = 5)
    private String languageCode;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;
}
