package com.devsam.shopmanagement.service.order_service;

import com.devsam.shopmanagement.dtos.OrderRequest;
import com.devsam.shopmanagement.entity.Order;

public interface OrderService {
    Order createOrder(OrderRequest orderRequest);

//    create an order

}
