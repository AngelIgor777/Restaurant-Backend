package org.test.restaurant_service.entity.translations;


import lombok.*;
import org.test.restaurant_service.entity.Language;
import org.test.restaurant_service.entity.Product;

import javax.persistence.*;


@Entity
@Table(name = "product_i18n", schema = "restaurant_service")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductI18n {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lang_id", referencedColumnName = "id")
    private Language language;

    private String name;

    private String description;
}
