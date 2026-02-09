package com.turkcell.etradedemo7.business.dtos.responses.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatedProductResponse {

    private int id;
    private String name;
    private String description;
    private BigDecimal unitPrice;
    private int stockQuantity;
    private LocalDateTime createdDate;
}
