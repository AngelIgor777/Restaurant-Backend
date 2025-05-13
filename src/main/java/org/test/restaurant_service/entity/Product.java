package org.test.restaurant_service.entity;

import javax.persistence.Table;
import javax.persistence.*;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@ToString(exclude = "type")
@Entity
@Table(name = "products", schema = "restaurant_service")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String description;

    @ManyToOne
    @JoinColumn(name = "type_id", foreignKey = @ForeignKey(name = "fk_product_type"))
    private ProductType type;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    private LocalTime cookingTime;

    @Column(nullable = false)
    private Boolean available = true;

    @Builder.Default
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Photo> photos = new ArrayList<>();


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id) && Objects.equals(name, product.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}

