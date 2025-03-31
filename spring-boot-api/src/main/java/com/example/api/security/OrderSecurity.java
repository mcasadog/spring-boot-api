package com.example.api.security;

import com.example.api.model.Order;
import com.example.api.model.OrderItem;
import com.example.api.repository.OrderItemRepository;
import com.example.api.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("orderSecurity")
@RequiredArgsConstructor
public class OrderSecurity {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public boolean isOrderOwner(Long orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            return false;
        }
        
        Order order = orderOpt.get();
        return order.getUser().getUsername().equals(currentUsername);
    }
    
    public boolean isOrderItemVisible(Long orderItemId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        
        Optional<OrderItem> orderItemOpt = orderItemRepository.findById(orderItemId);
        if (orderItemOpt.isEmpty()) {
            return false;
        }
        
        OrderItem orderItem = orderItemOpt.get();
        return orderItem.getOrder().getUser().getUsername().equals(currentUsername);
    }
}
