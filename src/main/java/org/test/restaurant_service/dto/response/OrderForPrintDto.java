package org.test.restaurant_service.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class OrderForPrintDto {
    private Integer table;
    private String phoneNumber;
    List<ProductItem> productItemList;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
    private String paymentMethod;

    public OrderForPrintDto(Integer table, String phoneNumber, List<ProductItem> productItemList, BigDecimal totalPrice, LocalDateTime createdAt, String paymentMethod) {
        this.table = table;
        this.phoneNumber = phoneNumber;
        this.productItemList = productItemList;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
        this.paymentMethod = paymentMethod;
    }

    public OrderForPrintDto() {
    }

    public Integer getTable() {
        return table;
    }

    public void setTable(Integer table) {
        this.table = table;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<ProductItem> getProductItemList() {
        return productItemList;
    }

    public void setProductItemList(List<ProductItem> productItemList) {
        this.productItemList = productItemList;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalPrice, createdAt);
    }

    @Override
    public String toString() {
        return "OrderForPrintDto{" +
                "table=" + table +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", productItemList=" + productItemList +
                ", totalPrice=" + totalPrice +
                ", createdAt=" + createdAt +
                ", paymentMethod='" + paymentMethod + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderForPrintDto order)) return false;
        return Objects.equals(totalPrice, order.totalPrice) && Objects.equals(createdAt, order.createdAt);
    }

}