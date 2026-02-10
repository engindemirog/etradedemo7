package com.turkcell.etradedemo7.business.concretes;

import com.turkcell.etradedemo7.business.abstracts.ProductService;
import com.turkcell.etradedemo7.business.dtos.requests.product.CreateProductRequest;
import com.turkcell.etradedemo7.business.dtos.requests.product.UpdateProductRequest;
import com.turkcell.etradedemo7.business.dtos.responses.product.CreatedProductResponse;
import com.turkcell.etradedemo7.business.dtos.responses.product.GetAllProductsResponse;
import com.turkcell.etradedemo7.business.dtos.responses.product.GetProductResponse;
import com.turkcell.etradedemo7.business.dtos.responses.product.UpdatedProductResponse;
import com.turkcell.etradedemo7.business.rules.ProductBusinessRules;
import com.turkcell.etradedemo7.dataAccess.CategoryRepository;
import com.turkcell.etradedemo7.dataAccess.ProductRepository;
import com.turkcell.etradedemo7.entities.Category;
import com.turkcell.etradedemo7.entities.Product;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductBusinessRules productBusinessRules;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, ProductBusinessRules productBusinessRules) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productBusinessRules = productBusinessRules;
    }

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
                    if (product.getCategory() != null) {
                        response.setCategoryId(product.getCategory().getId());
                        response.setCategoryName(product.getCategory().getName());
                    }
                    return response;
                })
                .toList();
    }

    @Override
    public GetProductResponse getById(int id) {
        productBusinessRules.checkIfProductExists(id);

        Product product = productRepository.findById(id).orElseThrow();

        GetProductResponse response = new GetProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setUnitPrice(product.getUnitPrice());
        response.setStockQuantity(product.getStockQuantity());
        if (product.getCategory() != null) {
            response.setCategoryId(product.getCategory().getId());
            response.setCategoryName(product.getCategory().getName());
        }
        response.setCreatedDate(product.getCreatedDate());
        response.setUpdatedDate(product.getUpdatedDate());
        response.setActive(product.isActive());
        return response;
    }

    @Override
    public CreatedProductResponse add(CreateProductRequest request) {
        productBusinessRules.checkIfProductNameAlreadyExists(request.getName());
        productBusinessRules.checkIfCategoryExists(request.getCategoryId());

        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow();

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setUnitPrice(request.getUnitPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(category);
        product.setCreatedDate(LocalDateTime.now());
        product.setActive(true);

        Product savedProduct = productRepository.save(product);

        CreatedProductResponse response = new CreatedProductResponse();
        response.setId(savedProduct.getId());
        response.setName(savedProduct.getName());
        response.setDescription(savedProduct.getDescription());
        response.setUnitPrice(savedProduct.getUnitPrice());
        response.setStockQuantity(savedProduct.getStockQuantity());
        response.setCategoryId(savedProduct.getCategory().getId());
        response.setCreatedDate(savedProduct.getCreatedDate());
        return response;
    }

    @Override
    public UpdatedProductResponse update(UpdateProductRequest request) {
        productBusinessRules.checkIfProductExists(request.getId());
        productBusinessRules.checkIfProductNameAlreadyExistsForUpdate(request.getId(), request.getName());
        productBusinessRules.checkIfCategoryExists(request.getCategoryId());

        Product product = productRepository.findById(request.getId()).orElseThrow();
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow();

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setUnitPrice(request.getUnitPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(category);
        product.setUpdatedDate(LocalDateTime.now());

        Product updatedProduct = productRepository.save(product);

        UpdatedProductResponse response = new UpdatedProductResponse();
        response.setId(updatedProduct.getId());
        response.setName(updatedProduct.getName());
        response.setDescription(updatedProduct.getDescription());
        response.setUnitPrice(updatedProduct.getUnitPrice());
        response.setStockQuantity(updatedProduct.getStockQuantity());
        response.setCategoryId(updatedProduct.getCategory().getId());
        response.setUpdatedDate(updatedProduct.getUpdatedDate());
        return response;
    }

    @Override
    public void delete(int id) {
        productBusinessRules.checkIfProductExists(id);

        Product product = productRepository.findById(id).orElseThrow();
        product.setActive(false);
        product.setDeletedDate(LocalDateTime.now());
        productRepository.save(product);
    }
}
