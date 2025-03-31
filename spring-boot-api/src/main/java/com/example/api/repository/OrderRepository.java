package com.example.api.repository;

import com.example.api.model.Order;
import com.example.api.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    List<Order> findByUser(User user);
    
    Page<Order> findByUser(User user, Pageable pageable);
    
    List<Order> findByStatus(Order.OrderStatus status);
    
    List<Order> findByUserAndStatus(User user, Order.OrderStatus status);
    
    List<Order> findByCreatedAtBetween(Instant startDate, Instant endDate);
}
