package org.test.restaurant_service.entity.translations;

import lombok.*;
import org.test.restaurant_service.entity.Language;
import org.test.restaurant_service.entity.ProductType;

import javax.persistence.*;

@Entity
@Table(
        name = "product_type_i18n",
        schema = "restaurant_service",
        uniqueConstraints = @UniqueConstraint(columnNames = {"product_type_id", "lang_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductTypeI18n {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_type_id", referencedColumnName = "id")
    private ProductType productType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lang_id", referencedColumnName = "id")
    private Language language;

    private String name;
}