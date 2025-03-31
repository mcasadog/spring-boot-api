package com.example.api.service.impl;

import com.example.api.dto.OrderItemDto;
import com.example.api.model.Order;
import com.example.api.model.OrderItem;
import com.example.api.model.Product;
import com.example.api.repository.OrderItemRepository;
import com.example.api.repository.OrderRepository;
import com.example.api.repository.ProductRepository;
import com.example.api.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemDto> getAllOrderItems() {
        return orderItemRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderItemDto> getOrderItemById(Long id) {
        return orderItemRepository.findById(id)
                .map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemDto> getOrderItemsByOrderId(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        
        return orderItemRepository.findByOrder(order).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemDto> getOrderItemsByProductId(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        
        return orderItemRepository.findByProduct(product).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderItemDto createOrderItem(OrderItemDto orderItemDto) {
        OrderItem orderItem = new OrderItem();
        
        // Set order
        Order order = orderRepository.findById(orderItemDto.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderItemDto.getOrderId()));
        orderItem.setOrder(order);
        
        // Set product
        Product product = productRepository.findById(orderItemDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + orderItemDto.getProductId()));
        orderItem.setProduct(product);
        
        // Set other fields
        orderItem.setQuantity(orderItemDto.getQuantity());
        orderItem.setPrice(orderItemDto.getPrice());
        
        // Save order item
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);
        
        return mapToDto(savedOrderItem);
    }

    @Override
    public OrderItemDto updateOrderItem(Long id, OrderItemDto orderItemDto) {
        OrderItem existingOrderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order item not found with id: " + id));
        
        // Update fields
        if (orderItemDto.getQuantity() != null) {
            existingOrderItem.setQuantity(orderItemDto.getQuantity());
        }
        
        if (orderItemDto.getPrice() != null) {
            existingOrderItem.setPrice(orderItemDto.getPrice());
        }
        
        // Update product if specified
        if (orderItemDto.getProductId() != null) {
            Product product = productRepository.findById(orderItemDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + orderItemDto.getProductId()));
            existingOrderItem.setProduct(product);
        }
        
        OrderItem updatedOrderItem = orderItemRepository.save(existingOrderItem);
        return mapToDto(updatedOrderItem);
    }

    @Override
    public void deleteOrderItem(Long id) {
        orderItemRepository.deleteById(id);
    }
    
    // Helper methods for mapping between entity and DTO
    private OrderItemDto mapToDto(OrderItem orderItem) {
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
