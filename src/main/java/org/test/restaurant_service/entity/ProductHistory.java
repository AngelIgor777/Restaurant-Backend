package org.test.restaurant_service.entity;

import javax.persistence.Table;
import javax.persistence.*;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@ToString(exclude = {"product", "type"})
@Entity
@Table(name = "product_history", schema = "restaurant_service")
public class ProductHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private String photoUrl;

    @ManyToOne
    @JoinColumn(name = "type_id", referencedColumnName = "id")
    private ProductType type;

    @Column(nullable = false)
    private BigDecimal price;

    private LocalTime cookingTime;

    @Column(name = "changed_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime changedAt = LocalDateTime.now();


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductHistory that = (ProductHistory) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(price, that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price);
    }
}
