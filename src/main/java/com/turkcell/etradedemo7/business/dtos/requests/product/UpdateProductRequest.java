package com.turkcell.etradedemo7.business.dtos.requests.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
