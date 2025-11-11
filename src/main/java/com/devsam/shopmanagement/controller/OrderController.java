package com.devsam.shopmanagement.controller;


import com.devsam.shopmanagement.dtos.OrderRequest;
import com.devsam.shopmanagement.entity.Order;
import com.devsam.shopmanagement.service.order_service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("api/v1/orders/")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
@PostMapping
public Order OrderController(OrderRequest orderRequest) {
        return orderService.createOrder(orderRequest);
    }

}
