package com.turkcell.etradedemo7.business.dtos.requests.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateCategoryRequest {

    @NotBlank(message = "Category name is required.")
    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters.")
    private String name;

    @Size(max = 500, message = "Description must be at most 500 characters.")
    private String description;

    public CreateCategoryRequest() {
    }

    public CreateCategoryRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
