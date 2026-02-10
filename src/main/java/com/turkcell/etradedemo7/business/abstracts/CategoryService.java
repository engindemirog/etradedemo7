package com.turkcell.etradedemo7.business.abstracts;

import com.turkcell.etradedemo7.business.dtos.requests.category.CreateCategoryRequest;
import com.turkcell.etradedemo7.business.dtos.requests.category.UpdateCategoryRequest;
import com.turkcell.etradedemo7.business.dtos.responses.category.CreatedCategoryResponse;
import com.turkcell.etradedemo7.business.dtos.responses.category.GetAllCategoriesResponse;
import com.turkcell.etradedemo7.business.dtos.responses.category.GetCategoryResponse;
import com.turkcell.etradedemo7.business.dtos.responses.category.UpdatedCategoryResponse;

import java.util.List;

public interface CategoryService {

    List<GetAllCategoriesResponse> getAll();

    GetCategoryResponse getById(int id);

    CreatedCategoryResponse add(CreateCategoryRequest request);

    UpdatedCategoryResponse update(UpdateCategoryRequest request);

    void delete(int id);
}
