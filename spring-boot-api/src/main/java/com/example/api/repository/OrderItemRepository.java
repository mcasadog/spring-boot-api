package com.example.api.repository;

import com.example.api.model.Order;
import com.example.api.model.OrderItem;
import com.example.api.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    List<OrderItem> findByOrder(Order order);
    
    List<OrderItem> findByProduct(Product product);
    
    List<OrderItem> findByOrderAndProduct(Order order, Product product);
}
