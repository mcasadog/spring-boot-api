package com.example.api.service;

import com.example.api.dto.OrderItemDto;

import java.util.List;
import java.util.Optional;

public interface OrderItemService {
    
    List<OrderItemDto> getAllOrderItems();
    
    Optional<OrderItemDto> getOrderItemById(Long id);
    
    List<OrderItemDto> getOrderItemsByOrderId(Long orderId);
    
    List<OrderItemDto> getOrderItemsByProductId(Long productId);
    
    OrderItemDto createOrderItem(OrderItemDto orderItemDto);
    
    OrderItemDto updateOrderItem(Long id, OrderItemDto orderItemDto);
    
    void deleteOrderItem(Long id);
}
