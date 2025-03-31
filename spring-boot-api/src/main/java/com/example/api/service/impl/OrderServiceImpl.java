package com.example.api.service.impl;

import com.example.api.dto.OrderDto;
import com.example.api.dto.OrderItemDto;
import com.example.api.model.Order;
import com.example.api.model.OrderItem;
import com.example.api.model.Product;
import com.example.api.model.User;
import com.example.api.repository.OrderRepository;
import com.example.api.repository.ProductRepository;
import com.example.api.repository.UserRepository;
import com.example.api.service.OrderItemService;
import com.example.api.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderItemService orderItemService;

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderDto> getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        return orderRepository.findByUser(user).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto> getOrdersByUserId(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        return orderRepository.findByUser(user, pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersByUserIdAndStatus(Long userId, Order.OrderStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        return orderRepository.findByUserAndStatus(user, status).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersByDateRange(Instant startDate, Instant endDate) {
        return orderRepository.findByCreatedAtBetween(startDate, endDate).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDto createOrder(OrderDto orderDto) {
        Order order = new Order();
        
        // Set user
        User user = userRepository.findById(orderDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + orderDto.getUserId()));
        order.setUser(user);
        
        // Set order details
        order.setTotalAmount(orderDto.getTotalAmount());
        order.setShippingAddress(orderDto.getShippingAddress());
        order.setBillingAddress(orderDto.getBillingAddress());
        order.setPaymentMethod(orderDto.getPaymentMethod());
        
        if (orderDto.getStatus() != null) {
            order.setStatus(Order.OrderStatus.valueOf(orderDto.getStatus()));
        } else {
            order.setStatus(Order.OrderStatus.PENDING);
        }
        
        // Save order first to get ID
        Order savedOrder = orderRepository.save(order);
        
        // Process order items if present
        if (orderDto.getOrderItems() != null && !orderDto.getOrderItems().isEmpty()) {
            for (OrderItemDto itemDto : orderDto.getOrderItems()) {
                OrderItemDto newItemDto = new OrderItemDto();
                newItemDto.setOrderId(savedOrder.getId());
                newItemDto.setProductId(itemDto.getProductId());
                newItemDto.setQuantity(itemDto.getQuantity());
                newItemDto.setPrice(itemDto.getPrice());
                
                orderItemService.createOrderItem(newItemDto);
            }
        }
        
        // Refresh order to include order items
        return mapToDto(orderRepository.findById(savedOrder.getId()).orElseThrow());
    }

    @Override
    public OrderDto updateOrder(Long id, OrderDto orderDto) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        
        // Update fields
        if (orderDto.getStatus() != null) {
            existingOrder.setStatus(Order.OrderStatus.valueOf(orderDto.getStatus()));
        }
        
        if (orderDto.getTotalAmount() != null) {
            existingOrder.setTotalAmount(orderDto.getTotalAmount());
        }
        
        if (orderDto.getShippingAddress() != null) {
            existingOrder.setShippingAddress(orderDto.getShippingAddress());
        }
        
        if (orderDto.getBillingAddress() != null) {
            existingOrder.setBillingAddress(orderDto.getBillingAddress());
        }
        
        if (orderDto.getPaymentMethod() != null) {
            existingOrder.setPaymentMethod(orderDto.getPaymentMethod());
        }
        
        Order updatedOrder = orderRepository.save(existingOrder);
        return mapToDto(updatedOrder);
    }

    @Override
    public OrderDto updateOrderStatus(Long id, Order.OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        
        return mapToDto(updatedOrder);
    }

    @Override
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
    
    // Helper methods for mapping between entity and DTO
    private OrderDto mapToDto(Order order) {
        OrderDto orderDto = OrderDto.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .shippingAddress(order.getShippingAddress())
                .billingAddress(order.getBillingAddress())
                .paymentMethod(order.getPaymentMethod())
                .build();
        
        // Map order items
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            List<OrderItemDto> orderItemDtos = order.getOrderItems().stream()
                    .map(this::mapOrderItemToDto)
                    .collect(Collectors.toList());
            
            orderDto.setOrderItems(orderItemDtos);
        }
        
        return orderDto;
    }
    
    private OrderItemDto mapOrderItemToDto(OrderItem orderItem) {
        return OrderItemDto.builder()
                .id(orderItem.getId())
                .orderId(orderItem.getOrder().getId())
                .productId(orderItem.getProduct().getId())
                .productName(orderItem.getProduct().getName())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .build();
    }
}
