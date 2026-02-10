package com.turkcell.etradedemo7.business.concretes;

import com.turkcell.etradedemo7.business.dtos.requests.category.CreateCategoryRequest;
import com.turkcell.etradedemo7.business.dtos.requests.category.UpdateCategoryRequest;
import com.turkcell.etradedemo7.business.dtos.responses.category.CreatedCategoryResponse;
import com.turkcell.etradedemo7.business.dtos.responses.category.GetAllCategoriesResponse;
import com.turkcell.etradedemo7.business.dtos.responses.category.GetCategoryResponse;
import com.turkcell.etradedemo7.business.dtos.responses.category.UpdatedCategoryResponse;
import com.turkcell.etradedemo7.business.rules.CategoryBusinessRules;
import com.turkcell.etradedemo7.core.exceptions.BusinessException;
import com.turkcell.etradedemo7.dataAccess.CategoryRepository;
import com.turkcell.etradedemo7.entities.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryBusinessRules categoryBusinessRules;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1);
        testCategory.setName("Elektronik");
        testCategory.setDescription("Elektronik ürünler");
        testCategory.setCreatedDate(LocalDateTime.of(2026, 2, 10, 14, 30));
        testCategory.setActive(true);
    }

    // ==================== getAll ====================

    @Nested
    @DisplayName("getAll")
    class GetAll {

        @Test
        @DisplayName("Should return all categories")
        void shouldReturnAllCategories() {
            Category category2 = new Category();
            category2.setId(2);
            category2.setName("Giyim");

            when(categoryRepository.findAll()).thenReturn(List.of(testCategory, category2));

            List<GetAllCategoriesResponse> result = categoryService.getAll();

            assertEquals(2, result.size());
            assertEquals(1, result.get(0).getId());
            assertEquals("Elektronik", result.get(0).getName());
            assertEquals(2, result.get(1).getId());
            assertEquals("Giyim", result.get(1).getName());
            verify(categoryRepository).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no categories exist")
        void shouldReturnEmptyList_whenNoCategories() {
            when(categoryRepository.findAll()).thenReturn(Collections.emptyList());

            List<GetAllCategoriesResponse> result = categoryService.getAll();

            assertTrue(result.isEmpty());
            verify(categoryRepository).findAll();
        }
    }

    // ==================== getById ====================

    @Nested
    @DisplayName("getById")
    class GetById {

        @Test
        @DisplayName("Should return category when found")
        void shouldReturnCategory_whenFound() {
            doNothing().when(categoryBusinessRules).checkIfCategoryExists(1);
            when(categoryRepository.findById(1)).thenReturn(Optional.of(testCategory));

            GetCategoryResponse result = categoryService.getById(1);

            assertEquals(1, result.getId());
            assertEquals("Elektronik", result.getName());
            assertEquals("Elektronik ürünler", result.getDescription());
            assertTrue(result.isActive());
            assertNotNull(result.getCreatedDate());
            verify(categoryBusinessRules).checkIfCategoryExists(1);
        }

        @Test
        @DisplayName("Should throw when category not found")
        void shouldThrow_whenCategoryNotFound() {
            doThrow(new BusinessException("Category not found with id: 99"))
                    .when(categoryBusinessRules).checkIfCategoryExists(99);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> categoryService.getById(99));

            assertEquals("Category not found with id: 99", exception.getMessage());
            verify(categoryRepository, never()).findById(anyInt());
        }
    }

    // ==================== add ====================

    @Nested
    @DisplayName("add")
    class Add {

        private CreateCategoryRequest createRequest;

        @BeforeEach
        void setUp() {
            createRequest = new CreateCategoryRequest();
            createRequest.setName("Elektronik");
            createRequest.setDescription("Elektronik ürünler");
        }

        @Test
        @DisplayName("Should create category successfully")
        void shouldCreateCategory() {
            doNothing().when(categoryBusinessRules).checkIfCategoryNameAlreadyExists("Elektronik");
            when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

            CreatedCategoryResponse result = categoryService.add(createRequest);

            assertEquals(1, result.getId());
            assertEquals("Elektronik", result.getName());
            assertEquals("Elektronik ürünler", result.getDescription());
            assertNotNull(result.getCreatedDate());

            verify(categoryBusinessRules).checkIfCategoryNameAlreadyExists("Elektronik");
            verify(categoryRepository).save(any(Category.class));
        }

        @Test
        @DisplayName("Should set correct entity fields before save")
        void shouldSetCorrectEntityFields() {
            doNothing().when(categoryBusinessRules).checkIfCategoryNameAlreadyExists("Elektronik");
            when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

            categoryService.add(createRequest);

            ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
            verify(categoryRepository).save(captor.capture());

            Category savedCategory = captor.getValue();
            assertEquals("Elektronik", savedCategory.getName());
            assertEquals("Elektronik ürünler", savedCategory.getDescription());
            assertTrue(savedCategory.isActive());
            assertNotNull(savedCategory.getCreatedDate());
        }

        @Test
        @DisplayName("Should throw when category name already exists")
        void shouldThrow_whenNameAlreadyExists() {
            doThrow(new BusinessException("Category already exists with name: Elektronik"))
                    .when(categoryBusinessRules).checkIfCategoryNameAlreadyExists("Elektronik");

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> categoryService.add(createRequest));

            assertEquals("Category already exists with name: Elektronik", exception.getMessage());
            verify(categoryRepository, never()).save(any());
        }
    }

    // ==================== update ====================

    @Nested
    @DisplayName("update")
    class Update {

        private UpdateCategoryRequest updateRequest;

        @BeforeEach
        void setUp() {
            updateRequest = new UpdateCategoryRequest();
            updateRequest.setId(1);
            updateRequest.setName("Elektronik Cihazlar");
            updateRequest.setDescription("Tüm elektronik ürünler");
        }

        @Test
        @DisplayName("Should update category successfully")
        void shouldUpdateCategory() {
            doNothing().when(categoryBusinessRules).checkIfCategoryExists(1);
            doNothing().when(categoryBusinessRules).checkIfCategoryNameAlreadyExistsForUpdate(1, "Elektronik Cihazlar");
            when(categoryRepository.findById(1)).thenReturn(Optional.of(testCategory));

            Category updatedCategory = new Category();
            updatedCategory.setId(1);
            updatedCategory.setName("Elektronik Cihazlar");
            updatedCategory.setDescription("Tüm elektronik ürünler");
            updatedCategory.setUpdatedDate(LocalDateTime.now());

            when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);

            UpdatedCategoryResponse result = categoryService.update(updateRequest);

            assertEquals(1, result.getId());
            assertEquals("Elektronik Cihazlar", result.getName());
            assertEquals("Tüm elektronik ürünler", result.getDescription());
            assertNotNull(result.getUpdatedDate());

            verify(categoryBusinessRules).checkIfCategoryExists(1);
            verify(categoryBusinessRules).checkIfCategoryNameAlreadyExistsForUpdate(1, "Elektronik Cihazlar");
        }

        @Test
        @DisplayName("Should throw when category not found for update")
        void shouldThrow_whenCategoryNotFoundForUpdate() {
            doThrow(new BusinessException("Category not found with id: 1"))
                    .when(categoryBusinessRules).checkIfCategoryExists(1);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> categoryService.update(updateRequest));

            assertEquals("Category not found with id: 1", exception.getMessage());
            verify(categoryRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw when updated name conflicts with another category")
        void shouldThrow_whenNameConflicts() {
            doNothing().when(categoryBusinessRules).checkIfCategoryExists(1);
            doThrow(new BusinessException("Category already exists with name: Elektronik Cihazlar"))
                    .when(categoryBusinessRules).checkIfCategoryNameAlreadyExistsForUpdate(1, "Elektronik Cihazlar");

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> categoryService.update(updateRequest));

            assertEquals("Category already exists with name: Elektronik Cihazlar", exception.getMessage());
            verify(categoryRepository, never()).save(any());
        }
    }

    // ==================== delete ====================

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("Should soft delete category successfully")
        void shouldSoftDeleteCategory() {
            doNothing().when(categoryBusinessRules).checkIfCategoryExists(1);
            when(categoryRepository.findById(1)).thenReturn(Optional.of(testCategory));
            when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

            categoryService.delete(1);

            ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
            verify(categoryRepository).save(captor.capture());

            Category deletedCategory = captor.getValue();
            assertFalse(deletedCategory.isActive());
            assertNotNull(deletedCategory.getDeletedDate());
        }

        @Test
        @DisplayName("Should throw when category not found for delete")
        void shouldThrow_whenCategoryNotFoundForDelete() {
            doThrow(new BusinessException("Category not found with id: 99"))
                    .when(categoryBusinessRules).checkIfCategoryExists(99);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> categoryService.delete(99));

            assertEquals("Category not found with id: 99", exception.getMessage());
            verify(categoryRepository, never()).findById(anyInt());
            verify(categoryRepository, never()).save(any());
        }
    }
}
