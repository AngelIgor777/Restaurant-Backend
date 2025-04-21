package org.test.restaurant_service.dto.response.printer;


import java.math.BigDecimal;

public class ProductItem {

    private String typeName;
    private String name;
    private Integer quantity;

    public ProductItem(String typeName, String name, Integer quantity, BigDecimal price) {
        this.typeName = typeName;
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getTypeName() {
        return typeName;
    }
}
