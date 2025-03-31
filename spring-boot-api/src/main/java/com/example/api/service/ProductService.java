package com.example.api.service;

import com.example.api.dto.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    
    List<ProductDto> getAllProducts();
    
    Page<ProductDto> getAllProducts(Pageable pageable);
    
    Page<ProductDto> getAllActiveProducts(Pageable pageable);
    
    Optional<ProductDto> getProductById(Long id);
    
    List<ProductDto> getProductsByCategory(String category);
    
    List<ProductDto> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    
    List<ProductDto> searchProductsByName(String name);
    
    ProductDto createProduct(ProductDto productDto);
    
    ProductDto updateProduct(Long id, ProductDto productDto);
    
    void deleteProduct(Long id);
    
    void activateProduct(Long id);
    
    void deactivateProduct(Long id);
}
