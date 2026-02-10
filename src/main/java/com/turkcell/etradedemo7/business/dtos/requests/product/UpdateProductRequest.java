package com.turkcell.etradedemo7.business.dtos.requests.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class UpdateProductRequest {

    @NotNull(message = "Product id is required.")
    private int id;

    @NotBlank(message = "Product name is required.")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters.")
    private String name;

    @Size(max = 500, message = "Description must be at most 500 characters.")
    private String description;

    @NotNull(message = "Unit price is required.")
    @Min(value = 0, message = "Unit price must be at least 0.")
    private BigDecimal unitPrice;

    @Min(value = 0, message = "Stock quantity must be at least 0.")
    private int stockQuantity;

    @NotNull(message = "Category id is required.")
    private int categoryId;

    public UpdateProductRequest() {
    }

    public UpdateProductRequest(int id, String name, String description, BigDecimal unitPrice, int stockQuantity, int categoryId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.unitPrice = unitPrice;
        this.stockQuantity = stockQuantity;
        this.categoryId = categoryId;
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
}
