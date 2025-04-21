package org.test.restaurant_service.dto.response.printer;

import java.util.List;

public class OrderForPrintDto {
    private Integer table;
    List<ProductItem> productItemList;
    private String createdAt;

    public OrderForPrintDto() {
    }

    public Integer getTable() {
        return table;
    }

    public void setTable(Integer table) {
        this.table = table;
    }

    public List<ProductItem> getProductItemList() {
        return productItemList;
    }

    public void setProductItemList(List<ProductItem> productItemList) {
        this.productItemList = productItemList;
    }
    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "OrderForPrintDto{" +
                "table=" + table +
                ", productItemList=" + productItemList +
                ", createdAt=" + createdAt +
                '}';
    }
}