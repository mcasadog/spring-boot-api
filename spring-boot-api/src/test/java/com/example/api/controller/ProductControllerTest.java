package com.example.api.controller;

import com.example.api.dto.ProductDto;
import com.example.api.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private ProductDto productDto1;
    private ProductDto productDto2;
    private List<ProductDto> productDtoList;
    private Page<ProductDto> productDtoPage;

    @BeforeEach
    void setUp() {
        productDto1 = ProductDto.builder()
                .id(1L)
                .name("Test Product 1")
                .description("Test Description 1")
                .price(new BigDecimal("99.99"))
                .quantity(10)
                .category("ELECTRONICS")
                .active(true)
                .build();

        productDto2 = ProductDto.builder()
                .id(2L)
                .name("Test Product 2")
                .description("Test Description 2")
                .price(new BigDecimal("149.99"))
                .quantity(5)
                .category("HOME_APPLIANCES")
                .active(true)
                .build();

        productDtoList = Arrays.asList(productDto1, productDto2);
        productDtoPage = new PageImpl<>(productDtoList);
    }

    @Test
    void getAllProductsShouldReturnProductsPage() {
        when(productService.getAllProducts(any(Pageable.class))).thenReturn(productDtoPage);

        ResponseEntity<Page<ProductDto>> response = productController.getAllProducts(Pageable.unpaged());

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().getContent().size());
    }

    @Test
    void getProductByIdShouldReturnProductWhenProductExists() {
        when(productService.getProductById(1L)).thenReturn(Optional.of(productDto1));

        ResponseEntity<ProductDto> response = productController.getProductById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Product 1", response.getBody().getName());
    }

    @Test
    void getProductsByPriceRangeShouldReturnFilteredProducts() {
        BigDecimal minPrice = new BigDecimal("50.00");
        BigDecimal maxPrice = new BigDecimal("150.00");
        
        when(productService.getProductsByPriceRange(eq(minPrice), eq(maxPrice))).thenReturn(productDtoList);

        ResponseEntity<List<ProductDto>> response = productController.getProductsByPriceRange(minPrice, maxPrice);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void createProductShouldReturnCreatedProduct() {
        when(productService.createProduct(any(ProductDto.class))).thenReturn(productDto1);

        ResponseEntity<ProductDto> response = productController.createProduct(productDto1);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Test Product 1", response.getBody().getName());
    }
}
