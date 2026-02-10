package com.turkcell.etradedemo7.business.rules;

import com.turkcell.etradedemo7.core.exceptions.BusinessException;
import com.turkcell.etradedemo7.dataAccess.CategoryRepository;
import com.turkcell.etradedemo7.dataAccess.ProductRepository;
import org.springframework.stereotype.Component;

@Component
public class ProductBusinessRules {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductBusinessRules(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public void checkIfProductExists(int id) {
        if (!productRepository.existsById(id)) {
            throw new BusinessException("Product not found with id: " + id);
        }
    }

    public void checkIfProductNameAlreadyExists(String name) {
        if (productRepository.existsByName(name)) {
            throw new BusinessException("Product already exists with name: " + name);
        }
    }

    public void checkIfProductNameAlreadyExistsForUpdate(int id, String name) {
        if (productRepository.existsByNameAndIdNot(name, id)) {
            throw new BusinessException("Product already exists with name: " + name);
        }
    }

    public void checkIfCategoryExists(int categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new BusinessException("Category not found with id: " + categoryId);
        }
    }
}
