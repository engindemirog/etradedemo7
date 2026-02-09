package com.turkcell.etradedemo7.business.concretes;

import com.turkcell.etradedemo7.business.abstracts.ProductService;
import com.turkcell.etradedemo7.business.dtos.requests.product.CreateProductRequest;
import com.turkcell.etradedemo7.business.dtos.requests.product.UpdateProductRequest;
import com.turkcell.etradedemo7.business.dtos.responses.product.CreatedProductResponse;
import com.turkcell.etradedemo7.business.dtos.responses.product.GetAllProductsResponse;
import com.turkcell.etradedemo7.business.dtos.responses.product.GetProductResponse;
import com.turkcell.etradedemo7.business.dtos.responses.product.UpdatedProductResponse;
import com.turkcell.etradedemo7.dataAccess.ProductRepository;
import com.turkcell.etradedemo7.entities.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public List<GetAllProductsResponse> getAll() {
        List<Product> products = productRepository.findAll();

        return products.stream()
                .map(product -> {
                    GetAllProductsResponse response = new GetAllProductsResponse();
                    response.setId(product.getId());
                    response.setName(product.getName());
                    response.setUnitPrice(product.getUnitPrice());
                    response.setStockQuantity(product.getStockQuantity());
                    return response;
                })
                .toList();
    }

    @Override
    public GetProductResponse getById(int id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        GetProductResponse response = new GetProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setUnitPrice(product.getUnitPrice());
        response.setStockQuantity(product.getStockQuantity());
        response.setCreatedDate(product.getCreatedDate());
        response.setUpdatedDate(product.getUpdatedDate());
        response.setActive(product.isActive());
        return response;
    }

    @Override
    public CreatedProductResponse add(CreateProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setUnitPrice(request.getUnitPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCreatedDate(LocalDateTime.now());
        product.setActive(true);

        Product savedProduct = productRepository.save(product);

        CreatedProductResponse response = new CreatedProductResponse();
        response.setId(savedProduct.getId());
        response.setName(savedProduct.getName());
        response.setDescription(savedProduct.getDescription());
        response.setUnitPrice(savedProduct.getUnitPrice());
        response.setStockQuantity(savedProduct.getStockQuantity());
        response.setCreatedDate(savedProduct.getCreatedDate());
        return response;
    }

    @Override
    public UpdatedProductResponse update(UpdateProductRequest request) {
        Product product = productRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + request.getId()));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setUnitPrice(request.getUnitPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setUpdatedDate(LocalDateTime.now());

        Product updatedProduct = productRepository.save(product);

        UpdatedProductResponse response = new UpdatedProductResponse();
        response.setId(updatedProduct.getId());
        response.setName(updatedProduct.getName());
        response.setDescription(updatedProduct.getDescription());
        response.setUnitPrice(updatedProduct.getUnitPrice());
        response.setStockQuantity(updatedProduct.getStockQuantity());
        response.setUpdatedDate(updatedProduct.getUpdatedDate());
        return response;
    }

    @Override
    public void delete(int id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        product.setActive(false);
        product.setDeletedDate(LocalDateTime.now());
        productRepository.save(product);
    }
}
