package com.turkcell.etradedemo7.business.dtos.requests.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UpdateCategoryRequest {

    @NotNull(message = "Category id is required.")
    private int id;

    @NotBlank(message = "Category name is required.")
    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters.")
    private String name;

    @Size(max = 500, message = "Description must be at most 500 characters.")
    private String description;

    public UpdateCategoryRequest() {
    }

    public UpdateCategoryRequest(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
