package com.turkcell.etradedemo7.api;

import com.turkcell.etradedemo7.business.abstracts.CategoryService;
import com.turkcell.etradedemo7.business.dtos.requests.category.CreateCategoryRequest;
import com.turkcell.etradedemo7.business.dtos.requests.category.UpdateCategoryRequest;
import com.turkcell.etradedemo7.business.dtos.responses.category.CreatedCategoryResponse;
import com.turkcell.etradedemo7.business.dtos.responses.category.GetAllCategoriesResponse;
import com.turkcell.etradedemo7.business.dtos.responses.category.GetCategoryResponse;
import com.turkcell.etradedemo7.business.dtos.responses.category.UpdatedCategoryResponse;
import jakarta.validation.Valid;
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
@RequestMapping("/api/categories")
public class CategoriesController {

    private final CategoryService categoryService;

    public CategoriesController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<GetAllCategoriesResponse> getAll() {
        return categoryService.getAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public GetCategoryResponse getById(@PathVariable int id) {
        return categoryService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreatedCategoryResponse add(@Valid @RequestBody CreateCategoryRequest request) {
        return categoryService.add(request);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public UpdatedCategoryResponse update(@Valid @RequestBody UpdateCategoryRequest request) {
        return categoryService.update(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        categoryService.delete(id);
    }
}
