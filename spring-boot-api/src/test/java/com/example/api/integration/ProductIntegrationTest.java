package com.example.api.integration;

import com.example.api.dto.ProductDto;
import com.example.api.model.Product;
import com.example.api.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        
        testProduct = new Product();
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(new BigDecimal("99.99"));
        testProduct.setQuantity(10);
        testProduct.setCategory("ELECTRONICS");
        testProduct.setActive(true);
        
        testProduct = productRepository.save(testProduct);
    }

    @Test
    void getAllProductsShouldReturnProducts() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value("Test Product"));
    }

    @Test
    void getProductByIdShouldReturnProduct() throws Exception {
        mockMvc.perform(get("/api/products/" + testProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.price").value(99.99));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProductShouldReturnCreatedProduct() throws Exception {
        ProductDto newProduct = new ProductDto();
        newProduct.setName("New Product");
        newProduct.setDescription("New Description");
        newProduct.setPrice(new BigDecimal("149.99"));
        newProduct.setQuantity(5);
        newProduct.setCategory("HOME_APPLIANCES");
        newProduct.setActive(true);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Product"))
                .andExpect(jsonPath("$.price").value(149.99));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProductShouldReturnUpdatedProduct() throws Exception {
        ProductDto updatedProduct = new ProductDto();
        updatedProduct.setName("Updated Product");
        updatedProduct.setPrice(new BigDecimal("199.99"));

        mockMvc.perform(put("/api/products/" + testProduct.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.price").value(199.99));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteProductShouldRemoveProduct() throws Exception {
        mockMvc.perform(delete("/api/products/" + testProduct.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/products/" + testProduct.getId()))
                .andExpect(status().isNotFound());
    }
}
