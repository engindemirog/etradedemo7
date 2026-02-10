package com.turkcell.etradedemo7.business.concretes;

import com.turkcell.etradedemo7.business.abstracts.CategoryService;
import com.turkcell.etradedemo7.business.dtos.requests.category.CreateCategoryRequest;
import com.turkcell.etradedemo7.business.dtos.requests.category.UpdateCategoryRequest;
import com.turkcell.etradedemo7.business.dtos.responses.category.CreatedCategoryResponse;
import com.turkcell.etradedemo7.business.dtos.responses.category.GetAllCategoriesResponse;
import com.turkcell.etradedemo7.business.dtos.responses.category.GetCategoryResponse;
import com.turkcell.etradedemo7.business.dtos.responses.category.UpdatedCategoryResponse;
import com.turkcell.etradedemo7.business.rules.CategoryBusinessRules;
import com.turkcell.etradedemo7.dataAccess.CategoryRepository;
import com.turkcell.etradedemo7.entities.Category;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryBusinessRules categoryBusinessRules;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryBusinessRules categoryBusinessRules) {
        this.categoryRepository = categoryRepository;
        this.categoryBusinessRules = categoryBusinessRules;
    }

    @Override
    public List<GetAllCategoriesResponse> getAll() {
        List<Category> categories = categoryRepository.findAll();

        return categories.stream()
                .map(category -> {
                    GetAllCategoriesResponse response = new GetAllCategoriesResponse();
                    response.setId(category.getId());
                    response.setName(category.getName());
                    return response;
                })
                .toList();
    }

    @Override
    public GetCategoryResponse getById(int id) {
        categoryBusinessRules.checkIfCategoryExists(id);

        Category category = categoryRepository.findById(id).orElseThrow();

        GetCategoryResponse response = new GetCategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setCreatedDate(category.getCreatedDate());
        response.setUpdatedDate(category.getUpdatedDate());
        response.setActive(category.isActive());
        return response;
    }

    @Override
    public CreatedCategoryResponse add(CreateCategoryRequest request) {
        categoryBusinessRules.checkIfCategoryNameAlreadyExists(request.getName());

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setCreatedDate(LocalDateTime.now());
        category.setActive(true);

        Category savedCategory = categoryRepository.save(category);

        CreatedCategoryResponse response = new CreatedCategoryResponse();
        response.setId(savedCategory.getId());
        response.setName(savedCategory.getName());
        response.setDescription(savedCategory.getDescription());
        response.setCreatedDate(savedCategory.getCreatedDate());
        return response;
    }

    @Override
    public UpdatedCategoryResponse update(UpdateCategoryRequest request) {
        categoryBusinessRules.checkIfCategoryExists(request.getId());
        categoryBusinessRules.checkIfCategoryNameAlreadyExistsForUpdate(request.getId(), request.getName());

        Category category = categoryRepository.findById(request.getId()).orElseThrow();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setUpdatedDate(LocalDateTime.now());

        Category updatedCategory = categoryRepository.save(category);

        UpdatedCategoryResponse response = new UpdatedCategoryResponse();
        response.setId(updatedCategory.getId());
        response.setName(updatedCategory.getName());
        response.setDescription(updatedCategory.getDescription());
        response.setUpdatedDate(updatedCategory.getUpdatedDate());
        return response;
    }

    @Override
    public void delete(int id) {
        categoryBusinessRules.checkIfCategoryExists(id);

        Category category = categoryRepository.findById(id).orElseThrow();
        category.setActive(false);
        category.setDeletedDate(LocalDateTime.now());
        categoryRepository.save(category);
    }
}
