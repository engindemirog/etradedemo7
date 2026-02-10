package com.turkcell.etradedemo7.business.dtos.responses.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UpdatedProductResponse {

    private int id;
    private String name;
    private String description;
    private BigDecimal unitPrice;
    private int stockQuantity;
    private int categoryId;
    private LocalDateTime updatedDate;

    public UpdatedProductResponse() {
    }

    public UpdatedProductResponse(int id, String name, String description, BigDecimal unitPrice, int stockQuantity, int categoryId, LocalDateTime updatedDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.unitPrice = unitPrice;
        this.stockQuantity = stockQuantity;
        this.categoryId = categoryId;
        this.updatedDate = updatedDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }
}
