package com.example.api.service;

import com.example.api.dto.OrderDto;
import com.example.api.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    
    List<OrderDto> getAllOrders();
    
    Page<OrderDto> getAllOrders(Pageable pageable);
    
    Optional<OrderDto> getOrderById(Long id);
    
    List<OrderDto> getOrdersByUserId(Long userId);
    
    Page<OrderDto> getOrdersByUserId(Long userId, Pageable pageable);
    
    List<OrderDto> getOrdersByStatus(Order.OrderStatus status);
    
    List<OrderDto> getOrdersByUserIdAndStatus(Long userId, Order.OrderStatus status);
    
    List<OrderDto> getOrdersByDateRange(Instant startDate, Instant endDate);
    
    OrderDto createOrder(OrderDto orderDto);
    
    OrderDto updateOrder(Long id, OrderDto orderDto);
    
    OrderDto updateOrderStatus(Long id, Order.OrderStatus status);
    
    void deleteOrder(Long id);
}
