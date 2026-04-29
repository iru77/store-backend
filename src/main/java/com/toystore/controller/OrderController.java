package com.toystore.controller;

import com.toystore.model.*;
import com.toystore.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin
public class OrderController {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    // ===== PLACE ORDER =====
    @PostMapping
    public ResponseEntity<?> placeOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody OrderRequest request) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate items
        if (request.items() == null || request.items().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Order must have at least one item"));
        }

        // Build order
        Order order = Order.builder()
                .user(user)
                .shippingAddress(request.shippingAddress())
                .status(Order.OrderStatus.PENDING)
                .build();

        // Build order items
        List<OrderItem> orderItems = request.items().stream().map(item -> {
            Product product = productRepository.findById(item.productId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.productId()));

            if (product.getStock() < item.qty()) {
                throw new RuntimeException("Insufficient stock for: " + product.getName());
            }

            // Reduce stock
            product.setStock(product.getStock() - item.qty());
            productRepository.save(product);

            return OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(item.qty())
                    .unitPrice(BigDecimal.valueOf(item.price()))
                    .build();
        }).collect(Collectors.toList());

        // Calculate total
        BigDecimal total = orderItems.stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setItems(orderItems);
        order.setTotalAmount(total);

        Order saved = orderRepository.save(order);

        return ResponseEntity.ok(Map.of(
                "orderId", saved.getId(),
                "total", total,
                "status", saved.getStatus().name(),
                "message", "Order placed successfully! 🎉"
        ));
    }

    // ===== GET MY ORDERS =====
    @GetMapping("/my")
    public ResponseEntity<List<Order>> getMyOrders(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        return ResponseEntity.ok(orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId()));
    }

    // ===== GET ORDER BY ID =====
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        return orderRepository.findById(id)
                .filter(o -> o.getUser().getEmail().equals(userDetails.getUsername()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ===== DTOs =====
    public record OrderRequest(
            List<CartItem> items,
            String shippingAddress
    ) {}

    public record CartItem(Long productId, int qty, double price) {}
}
