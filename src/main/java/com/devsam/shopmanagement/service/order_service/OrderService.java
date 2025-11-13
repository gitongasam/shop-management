package com.devsam.shopmanagement.service.order_service;

import com.devsam.shopmanagement.dtos.OrderRequest;
import com.devsam.shopmanagement.entity.Order;
import com.devsam.shopmanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OrderService {
    Order createOrder(OrderRequest orderRequest, User user);

    Page<Order> getAllOrders(Pageable pageable, User user);

// get order by id
    Order getOrderById(UUID id);

//    update order by id
    Order updateOrder(OrderRequest orderRequest, User user,UUID id);


}
