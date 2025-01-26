package org.test.restaurant_service.entity;

import javax.persistence.Table;
import javax.persistence.*;

import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;

@ToString(exclude = {"order", "product"})
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Entity
@Table(name = "order_products", schema = "restaurant_service",
        uniqueConstraints = @UniqueConstraint(name = "unique_order_item", columnNames = {"order_id", "product_id"}))
public class OrderProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "order_id", foreignKey = @ForeignKey(name = "fk_order_product_order"), nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id", foreignKey = @ForeignKey(name = "fk_order_product_product"), nullable = false)
    private Product product;


    @Column(name = "price_with_discount", precision = 10, scale = 2)
    private BigDecimal priceWithDiscount;

    @Column(nullable = false)
    private Integer quantity;


    public boolean hasDiscount() {
        return priceWithDiscount != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderProduct that = (OrderProduct) o;
        return Objects.equals(order, that.order) &&
                Objects.equals(product, that.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, product);
    }
}