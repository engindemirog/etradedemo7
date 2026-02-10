package com.turkcell.etradedemo7.business.rules;

import com.turkcell.etradedemo7.core.exceptions.BusinessException;
import com.turkcell.etradedemo7.dataAccess.CategoryRepository;
import org.springframework.stereotype.Component;

@Component
public class CategoryBusinessRules {

    private final CategoryRepository categoryRepository;

    public CategoryBusinessRules(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void checkIfCategoryExists(int id) {
        if (!categoryRepository.existsById(id)) {
            throw new BusinessException("Category not found with id: " + id);
        }
    }

    public void checkIfCategoryNameAlreadyExists(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new BusinessException("Category already exists with name: " + name);
        }
    }

    public void checkIfCategoryNameAlreadyExistsForUpdate(int id, String name) {
        if (categoryRepository.existsByNameAndIdNot(name, id)) {
            throw new BusinessException("Category already exists with name: " + name);
        }
    }
}
