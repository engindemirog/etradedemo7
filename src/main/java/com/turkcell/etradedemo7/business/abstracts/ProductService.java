package com.turkcell.etradedemo7.business.abstracts;

import com.turkcell.etradedemo7.business.dtos.requests.product.CreateProductRequest;
import com.turkcell.etradedemo7.business.dtos.requests.product.UpdateProductRequest;
import com.turkcell.etradedemo7.business.dtos.responses.product.CreatedProductResponse;
import com.turkcell.etradedemo7.business.dtos.responses.product.GetAllProductsResponse;
import com.turkcell.etradedemo7.business.dtos.responses.product.GetProductResponse;
import com.turkcell.etradedemo7.business.dtos.responses.product.UpdatedProductResponse;

import java.util.List;

public interface ProductService {

    List<GetAllProductsResponse> getAll();

    GetProductResponse getById(int id);

    CreatedProductResponse add(CreateProductRequest request);

    UpdatedProductResponse update(UpdateProductRequest request);

    void delete(int id);
}
