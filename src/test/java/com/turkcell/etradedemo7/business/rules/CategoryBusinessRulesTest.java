package com.turkcell.etradedemo7.business.rules;

import com.turkcell.etradedemo7.core.exceptions.BusinessException;
import com.turkcell.etradedemo7.dataAccess.CategoryRepository;
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
class CategoryBusinessRulesTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryBusinessRules categoryBusinessRules;

    @Nested
    @DisplayName("checkIfCategoryExists")
    class CheckIfCategoryExists {

        @Test
        @DisplayName("Should pass when category exists")
        void shouldPass_whenCategoryExists() {
            when(categoryRepository.existsById(1)).thenReturn(true);

            assertDoesNotThrow(() -> categoryBusinessRules.checkIfCategoryExists(1));
            verify(categoryRepository).existsById(1);
        }

        @Test
        @DisplayName("Should throw BusinessException when category does not exist")
        void shouldThrow_whenCategoryDoesNotExist() {
            when(categoryRepository.existsById(99)).thenReturn(false);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> categoryBusinessRules.checkIfCategoryExists(99));

            assertEquals("Category not found with id: 99", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("checkIfCategoryNameAlreadyExists")
    class CheckIfCategoryNameAlreadyExists {

        @Test
        @DisplayName("Should pass when name is unique")
        void shouldPass_whenNameIsUnique() {
            when(categoryRepository.existsByName("Elektronik")).thenReturn(false);

            assertDoesNotThrow(() -> categoryBusinessRules.checkIfCategoryNameAlreadyExists("Elektronik"));
            verify(categoryRepository).existsByName("Elektronik");
        }

        @Test
        @DisplayName("Should throw BusinessException when name already exists")
        void shouldThrow_whenNameAlreadyExists() {
            when(categoryRepository.existsByName("Elektronik")).thenReturn(true);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> categoryBusinessRules.checkIfCategoryNameAlreadyExists("Elektronik"));

            assertEquals("Category already exists with name: Elektronik", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("checkIfCategoryNameAlreadyExistsForUpdate")
    class CheckIfCategoryNameAlreadyExistsForUpdate {

        @Test
        @DisplayName("Should pass when name is unique for other records")
        void shouldPass_whenNameIsUniqueForOtherRecords() {
            when(categoryRepository.existsByNameAndIdNot("Elektronik", 1)).thenReturn(false);

            assertDoesNotThrow(() -> categoryBusinessRules.checkIfCategoryNameAlreadyExistsForUpdate(1, "Elektronik"));
            verify(categoryRepository).existsByNameAndIdNot("Elektronik", 1);
        }

        @Test
        @DisplayName("Should throw BusinessException when name exists for another record")
        void shouldThrow_whenNameExistsForAnotherRecord() {
            when(categoryRepository.existsByNameAndIdNot("Elektronik", 2)).thenReturn(true);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> categoryBusinessRules.checkIfCategoryNameAlreadyExistsForUpdate(2, "Elektronik"));

            assertEquals("Category already exists with name: Elektronik", exception.getMessage());
        }
    }
}
