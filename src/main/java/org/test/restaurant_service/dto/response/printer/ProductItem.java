package org.test.restaurant_service.dto.response.printer;


import java.math.BigDecimal;
import java.util.Objects;

public class ProductItem {

    private String typeName;
    private String name;
    private Integer quantity;
    protected BigDecimal price;

    public ProductItem(String typeName, String name, Integer quantity, BigDecimal price) {
        this.typeName = typeName;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public ProductItem() {
    }

    public String getTypeName() {
        return typeName;
    }

    public String getName() {
        return name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "ProductItem{" +
                "typeName='" + typeName + '\'' +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductItem that)) return false;
        return Objects.equals(typeName, that.typeName) && Objects.equals(name, that.name) && Objects.equals(quantity, that.quantity) && Objects.equals(price, that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeName, name, quantity, price);
    }
}
