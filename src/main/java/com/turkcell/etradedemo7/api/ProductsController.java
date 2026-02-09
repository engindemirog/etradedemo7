package com.turkcell.etradedemo7.api;

import com.turkcell.etradedemo7.business.abstracts.ProductService;
import com.turkcell.etradedemo7.business.dtos.requests.product.CreateProductRequest;
import com.turkcell.etradedemo7.business.dtos.requests.product.UpdateProductRequest;
import com.turkcell.etradedemo7.business.dtos.responses.product.CreatedProductResponse;
import com.turkcell.etradedemo7.business.dtos.responses.product.GetAllProductsResponse;
import com.turkcell.etradedemo7.business.dtos.responses.product.GetProductResponse;
import com.turkcell.etradedemo7.business.dtos.responses.product.UpdatedProductResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductsController {

    private final ProductService productService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<GetAllProductsResponse> getAll() {
        return productService.getAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public GetProductResponse getById(@PathVariable int id) {
        return productService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreatedProductResponse add(@Valid @RequestBody CreateProductRequest request) {
        return productService.add(request);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public UpdatedProductResponse update(@Valid @RequestBody UpdateProductRequest request) {
        return productService.update(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        productService.delete(id);
    }
}
