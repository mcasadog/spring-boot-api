package com.example.api.controller;

import com.example.api.dto.OrderItemDto;
import com.example.api.service.OrderItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderItemDto>> getAllOrderItems() {
        return ResponseEntity.ok(orderItemService.getAllOrderItems());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @orderSecurity.isOrderItemVisible(#id)")
    public ResponseEntity<OrderItemDto> getOrderItemById(@PathVariable Long id) {
        return orderItemService.getOrderItemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasRole('ADMIN') or @orderSecurity.isOrderOwner(#orderId)")
    public ResponseEntity<List<OrderItemDto>> getOrderItemsByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderItemService.getOrderItemsByOrderId(orderId));
    }
    
    @GetMapping("/product/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderItemDto>> getOrderItemsByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(orderItemService.getOrderItemsByProductId(productId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderItemDto> createOrderItem(@Valid @RequestBody OrderItemDto orderItemDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderItemService.createOrderItem(orderItemDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderItemDto> updateOrderItem(@PathVariable Long id, @Valid @RequestBody OrderItemDto orderItemDto) {
        return ResponseEntity.ok(orderItemService.updateOrderItem(id, orderItemDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Long id) {
        orderItemService.deleteOrderItem(id);
        return ResponseEntity.noContent().build();
    }
}
