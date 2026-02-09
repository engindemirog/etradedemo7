package com.turkcell.etradedemo7.business.dtos.responses.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetAllProductsResponse {

    private int id;
    private String name;
    private BigDecimal unitPrice;
    private int stockQuantity;
}
