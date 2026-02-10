package com.turkcell.etradedemo7.business.concretes;

import com.turkcell.etradedemo7.business.dtos.requests.product.CreateProductRequest;
import com.turkcell.etradedemo7.business.dtos.requests.product.UpdateProductRequest;
import com.turkcell.etradedemo7.business.dtos.responses.product.CreatedProductResponse;
import com.turkcell.etradedemo7.business.dtos.responses.product.GetAllProductsResponse;
import com.turkcell.etradedemo7.business.dtos.responses.product.GetProductResponse;
import com.turkcell.etradedemo7.business.dtos.responses.product.UpdatedProductResponse;
import com.turkcell.etradedemo7.business.rules.ProductBusinessRules;
import com.turkcell.etradedemo7.core.exceptions.BusinessException;
import com.turkcell.etradedemo7.dataAccess.CategoryRepository;
import com.turkcell.etradedemo7.dataAccess.ProductRepository;
import com.turkcell.etradedemo7.entities.Category;
import com.turkcell.etradedemo7.entities.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductBusinessRules productBusinessRules;

    @InjectMocks
    private ProductServiceImpl productService;

    private Category testCategory;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1);
        testCategory.setName("Elektronik");
        testCategory.setDescription("Elektronik ürünler");

        testProduct = new Product();
        testProduct.setId(1);
        testProduct.setName("iPhone 15");
        testProduct.setDescription("Apple iPhone 15");
        testProduct.setUnitPrice(new BigDecimal("59999.99"));
        testProduct.setStockQuantity(150);
        testProduct.setCategory(testCategory);
        testProduct.setCreatedDate(LocalDateTime.of(2026, 2, 10, 14, 30));
        testProduct.setActive(true);
    }

    // ==================== getAll ====================

    @Nested
    @DisplayName("getAll")
    class GetAll {

        @Test
        @DisplayName("Should return all products")
        void shouldReturnAllProducts() {
            Product product2 = new Product();
            product2.setId(2);
            product2.setName("Samsung Galaxy S24");
            product2.setUnitPrice(new BigDecimal("49999.99"));
            product2.setStockQuantity(200);
            product2.setCategory(testCategory);

            when(productRepository.findAll()).thenReturn(List.of(testProduct, product2));

            List<GetAllProductsResponse> result = productService.getAll();

            assertEquals(2, result.size());
            assertEquals("iPhone 15", result.get(0).getName());
            assertEquals(new BigDecimal("59999.99"), result.get(0).getUnitPrice());
            assertEquals(1, result.get(0).getCategoryId());
            assertEquals("Elektronik", result.get(0).getCategoryName());
            assertEquals("Samsung Galaxy S24", result.get(1).getName());
            verify(productRepository).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no products exist")
        void shouldReturnEmptyList_whenNoProducts() {
            when(productRepository.findAll()).thenReturn(Collections.emptyList());

            List<GetAllProductsResponse> result = productService.getAll();

            assertTrue(result.isEmpty());
            verify(productRepository).findAll();
        }

        @Test
        @DisplayName("Should handle products without category")
        void shouldHandleProductsWithoutCategory() {
            testProduct.setCategory(null);
            when(productRepository.findAll()).thenReturn(List.of(testProduct));

            List<GetAllProductsResponse> result = productService.getAll();

            assertEquals(1, result.size());
            assertEquals(0, result.get(0).getCategoryId());
            assertEquals(null, result.get(0).getCategoryName());
        }
    }

    // ==================== getById ====================

    @Nested
    @DisplayName("getById")
    class GetById {

        @Test
        @DisplayName("Should return product when found")
        void shouldReturnProduct_whenFound() {
            doNothing().when(productBusinessRules).checkIfProductExists(1);
            when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));

            GetProductResponse result = productService.getById(1);

            assertEquals(1, result.getId());
            assertEquals("iPhone 15", result.getName());
            assertEquals("Apple iPhone 15", result.getDescription());
            assertEquals(new BigDecimal("59999.99"), result.getUnitPrice());
            assertEquals(150, result.getStockQuantity());
            assertEquals(1, result.getCategoryId());
            assertEquals("Elektronik", result.getCategoryName());
            assertTrue(result.isActive());
            verify(productBusinessRules).checkIfProductExists(1);
        }

        @Test
        @DisplayName("Should throw when product not found")
        void shouldThrow_whenProductNotFound() {
            doThrow(new BusinessException("Product not found with id: 99"))
                    .when(productBusinessRules).checkIfProductExists(99);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> productService.getById(99));

            assertEquals("Product not found with id: 99", exception.getMessage());
            verify(productRepository, never()).findById(anyInt());
        }
    }

    // ==================== add ====================

    @Nested
    @DisplayName("add")
    class Add {

        private CreateProductRequest createRequest;

        @BeforeEach
        void setUp() {
            createRequest = new CreateProductRequest();
            createRequest.setName("iPhone 15");
            createRequest.setDescription("Apple iPhone 15");
            createRequest.setUnitPrice(new BigDecimal("59999.99"));
            createRequest.setStockQuantity(150);
            createRequest.setCategoryId(1);
        }

        @Test
        @DisplayName("Should create product successfully")
        void shouldCreateProduct() {
            doNothing().when(productBusinessRules).checkIfProductNameAlreadyExists("iPhone 15");
            doNothing().when(productBusinessRules).checkIfCategoryExists(1);
            when(categoryRepository.findById(1)).thenReturn(Optional.of(testCategory));
            when(productRepository.save(any(Product.class))).thenReturn(testProduct);

            CreatedProductResponse result = productService.add(createRequest);

            assertEquals(1, result.getId());
            assertEquals("iPhone 15", result.getName());
            assertEquals("Apple iPhone 15", result.getDescription());
            assertEquals(new BigDecimal("59999.99"), result.getUnitPrice());
            assertEquals(150, result.getStockQuantity());
            assertEquals(1, result.getCategoryId());
            assertNotNull(result.getCreatedDate());

            verify(productBusinessRules).checkIfProductNameAlreadyExists("iPhone 15");
            verify(productBusinessRules).checkIfCategoryExists(1);
            verify(productRepository).save(any(Product.class));
        }

        @Test
        @DisplayName("Should set correct entity fields before save")
        void shouldSetCorrectEntityFields() {
            doNothing().when(productBusinessRules).checkIfProductNameAlreadyExists(anyString());
            doNothing().when(productBusinessRules).checkIfCategoryExists(anyInt());
            when(categoryRepository.findById(1)).thenReturn(Optional.of(testCategory));
            when(productRepository.save(any(Product.class))).thenReturn(testProduct);

            productService.add(createRequest);

            ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
            verify(productRepository).save(captor.capture());

            Product savedProduct = captor.getValue();
            assertEquals("iPhone 15", savedProduct.getName());
            assertEquals("Apple iPhone 15", savedProduct.getDescription());
            assertEquals(new BigDecimal("59999.99"), savedProduct.getUnitPrice());
            assertEquals(150, savedProduct.getStockQuantity());
            assertEquals(testCategory, savedProduct.getCategory());
            assertTrue(savedProduct.isActive());
            assertNotNull(savedProduct.getCreatedDate());
        }

        @Test
        @DisplayName("Should throw when product name already exists")
        void shouldThrow_whenNameAlreadyExists() {
            doThrow(new BusinessException("Product already exists with name: iPhone 15"))
                    .when(productBusinessRules).checkIfProductNameAlreadyExists("iPhone 15");

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> productService.add(createRequest));

            assertEquals("Product already exists with name: iPhone 15", exception.getMessage());
            verify(productRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw when category does not exist")
        void shouldThrow_whenCategoryDoesNotExist() {
            doNothing().when(productBusinessRules).checkIfProductNameAlreadyExists("iPhone 15");
            doThrow(new BusinessException("Category not found with id: 1"))
                    .when(productBusinessRules).checkIfCategoryExists(1);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> productService.add(createRequest));

            assertEquals("Category not found with id: 1", exception.getMessage());
            verify(productRepository, never()).save(any());
        }
    }

    // ==================== update ====================

    @Nested
    @DisplayName("update")
    class Update {

        private UpdateProductRequest updateRequest;

        @BeforeEach
        void setUp() {
            updateRequest = new UpdateProductRequest();
            updateRequest.setId(1);
            updateRequest.setName("iPhone 15 Pro");
            updateRequest.setDescription("Apple iPhone 15 Pro");
            updateRequest.setUnitPrice(new BigDecimal("74999.99"));
            updateRequest.setStockQuantity(100);
            updateRequest.setCategoryId(1);
        }

        @Test
        @DisplayName("Should update product successfully")
        void shouldUpdateProduct() {
            doNothing().when(productBusinessRules).checkIfProductExists(1);
            doNothing().when(productBusinessRules).checkIfProductNameAlreadyExistsForUpdate(1, "iPhone 15 Pro");
            doNothing().when(productBusinessRules).checkIfCategoryExists(1);
            when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
            when(categoryRepository.findById(1)).thenReturn(Optional.of(testCategory));

            Product updatedProduct = new Product();
            updatedProduct.setId(1);
            updatedProduct.setName("iPhone 15 Pro");
            updatedProduct.setDescription("Apple iPhone 15 Pro");
            updatedProduct.setUnitPrice(new BigDecimal("74999.99"));
            updatedProduct.setStockQuantity(100);
            updatedProduct.setCategory(testCategory);
            updatedProduct.setUpdatedDate(LocalDateTime.now());

            when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

            UpdatedProductResponse result = productService.update(updateRequest);

            assertEquals(1, result.getId());
            assertEquals("iPhone 15 Pro", result.getName());
            assertEquals("Apple iPhone 15 Pro", result.getDescription());
            assertEquals(new BigDecimal("74999.99"), result.getUnitPrice());
            assertEquals(100, result.getStockQuantity());
            assertEquals(1, result.getCategoryId());
            assertNotNull(result.getUpdatedDate());

            verify(productBusinessRules).checkIfProductExists(1);
            verify(productBusinessRules).checkIfProductNameAlreadyExistsForUpdate(1, "iPhone 15 Pro");
            verify(productBusinessRules).checkIfCategoryExists(1);
        }

        @Test
        @DisplayName("Should throw when product not found for update")
        void shouldThrow_whenProductNotFoundForUpdate() {
            doThrow(new BusinessException("Product not found with id: 1"))
                    .when(productBusinessRules).checkIfProductExists(1);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> productService.update(updateRequest));

            assertEquals("Product not found with id: 1", exception.getMessage());
            verify(productRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw when updated name conflicts with another product")
        void shouldThrow_whenNameConflicts() {
            doNothing().when(productBusinessRules).checkIfProductExists(1);
            doThrow(new BusinessException("Product already exists with name: iPhone 15 Pro"))
                    .when(productBusinessRules).checkIfProductNameAlreadyExistsForUpdate(1, "iPhone 15 Pro");

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> productService.update(updateRequest));

            assertEquals("Product already exists with name: iPhone 15 Pro", exception.getMessage());
            verify(productRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw when category not found for update")
        void shouldThrow_whenCategoryNotFoundForUpdate() {
            doNothing().when(productBusinessRules).checkIfProductExists(1);
            doNothing().when(productBusinessRules).checkIfProductNameAlreadyExistsForUpdate(1, "iPhone 15 Pro");
            doThrow(new BusinessException("Category not found with id: 1"))
                    .when(productBusinessRules).checkIfCategoryExists(1);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> productService.update(updateRequest));

            assertEquals("Category not found with id: 1", exception.getMessage());
            verify(productRepository, never()).save(any());
        }
    }

    // ==================== delete ====================

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("Should soft delete product successfully")
        void shouldSoftDeleteProduct() {
            doNothing().when(productBusinessRules).checkIfProductExists(1);
            when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
            when(productRepository.save(any(Product.class))).thenReturn(testProduct);

            productService.delete(1);

            ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
            verify(productRepository).save(captor.capture());

            Product deletedProduct = captor.getValue();
            assertFalse(deletedProduct.isActive());
            assertNotNull(deletedProduct.getDeletedDate());
        }

        @Test
        @DisplayName("Should throw when product not found for delete")
        void shouldThrow_whenProductNotFoundForDelete() {
            doThrow(new BusinessException("Product not found with id: 99"))
                    .when(productBusinessRules).checkIfProductExists(99);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> productService.delete(99));

            assertEquals("Product not found with id: 99", exception.getMessage());
            verify(productRepository, never()).findById(anyInt());
            verify(productRepository, never()).save(any());
        }
    }
}
