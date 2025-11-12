package com.devsam.shopmanagement.controller;


import com.devsam.shopmanagement.dtos.OrderRequest;
import com.devsam.shopmanagement.dtos.OrderResponse;
import com.devsam.shopmanagement.entity.Order;
import com.devsam.shopmanagement.entity.User;
import com.devsam.shopmanagement.repository.UserRepository;
import com.devsam.shopmanagement.service.order_service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    private final UserRepository userRepository;
    @PostMapping
    public Order OrderController(@RequestBody OrderRequest orderRequest, @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found"));
        return orderService.createOrder(orderRequest, user);
    }

    @GetMapping
    public Page<OrderResponse> getAllOrders(Pageable pageable, @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found"));

        return orderService.getAllOrders(pageable, user);
    }

    @GetMapping("{id}")
    public Order getOrderById(@PathVariable UUID orderId) {
        return orderService.getOrderById(orderId);
    }

    @PutMapping("{id}")
    public Order updateOrder(@PathVariable UUID id, OrderRequest orderRequest, @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found"));
        return orderService.updateOrder(orderRequest, user, id);
    }


}
