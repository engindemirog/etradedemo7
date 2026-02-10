package com.turkcell.etradedemo7.business.rules;

import com.turkcell.etradedemo7.core.exceptions.BusinessException;
import com.turkcell.etradedemo7.dataAccess.CategoryRepository;
import com.turkcell.etradedemo7.dataAccess.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductBusinessRulesTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductBusinessRules productBusinessRules;

    @Nested
    @DisplayName("checkIfProductExists")
    class CheckIfProductExists {

        @Test
        @DisplayName("Should pass when product exists")
        void shouldPass_whenProductExists() {
            when(productRepository.existsById(1)).thenReturn(true);

            assertDoesNotThrow(() -> productBusinessRules.checkIfProductExists(1));
            verify(productRepository).existsById(1);
        }

        @Test
        @DisplayName("Should throw BusinessException when product does not exist")
        void shouldThrow_whenProductDoesNotExist() {
            when(productRepository.existsById(99)).thenReturn(false);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> productBusinessRules.checkIfProductExists(99));

            assertEquals("Product not found with id: 99", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("checkIfProductNameAlreadyExists")
    class CheckIfProductNameAlreadyExists {

        @Test
        @DisplayName("Should pass when name is unique")
        void shouldPass_whenNameIsUnique() {
            when(productRepository.existsByName("New Product")).thenReturn(false);

            assertDoesNotThrow(() -> productBusinessRules.checkIfProductNameAlreadyExists("New Product"));
            verify(productRepository).existsByName("New Product");
        }

        @Test
        @DisplayName("Should throw BusinessException when name already exists")
        void shouldThrow_whenNameAlreadyExists() {
            when(productRepository.existsByName("iPhone 15")).thenReturn(true);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> productBusinessRules.checkIfProductNameAlreadyExists("iPhone 15"));

            assertEquals("Product already exists with name: iPhone 15", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("checkIfProductNameAlreadyExistsForUpdate")
    class CheckIfProductNameAlreadyExistsForUpdate {

        @Test
        @DisplayName("Should pass when name is unique for other records")
        void shouldPass_whenNameIsUniqueForOtherRecords() {
            when(productRepository.existsByNameAndIdNot("iPhone 15", 1)).thenReturn(false);

            assertDoesNotThrow(() -> productBusinessRules.checkIfProductNameAlreadyExistsForUpdate(1, "iPhone 15"));
            verify(productRepository).existsByNameAndIdNot("iPhone 15", 1);
        }

        @Test
        @DisplayName("Should throw BusinessException when name exists for another record")
        void shouldThrow_whenNameExistsForAnotherRecord() {
            when(productRepository.existsByNameAndIdNot("iPhone 15", 2)).thenReturn(true);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> productBusinessRules.checkIfProductNameAlreadyExistsForUpdate(2, "iPhone 15"));

            assertEquals("Product already exists with name: iPhone 15", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("checkIfCategoryExists")
    class CheckIfCategoryExists {

        @Test
        @DisplayName("Should pass when category exists")
        void shouldPass_whenCategoryExists() {
            when(categoryRepository.existsById(1)).thenReturn(true);

            assertDoesNotThrow(() -> productBusinessRules.checkIfCategoryExists(1));
            verify(categoryRepository).existsById(1);
        }

        @Test
        @DisplayName("Should throw BusinessException when category does not exist")
        void shouldThrow_whenCategoryDoesNotExist() {
            when(categoryRepository.existsById(99)).thenReturn(false);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> productBusinessRules.checkIfCategoryExists(99));

            assertEquals("Category not found with id: 99", exception.getMessage());
        }
    }
}
