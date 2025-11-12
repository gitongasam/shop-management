package com.devsam.shopmanagement.service.order_service;

import com.devsam.shopmanagement.dtos.OrderItemRequest;
import com.devsam.shopmanagement.dtos.OrderItemResponse;
import com.devsam.shopmanagement.dtos.OrderRequest;
import com.devsam.shopmanagement.dtos.OrderResponse;
import com.devsam.shopmanagement.entity.Order;
import com.devsam.shopmanagement.entity.OrderItem;
import com.devsam.shopmanagement.entity.User;
import com.devsam.shopmanagement.errors.ResourceNotFoundException;
import com.devsam.shopmanagement.repository.CustomerRepository;
import com.devsam.shopmanagement.repository.OrderItemRepository;
import com.devsam.shopmanagement.repository.OrderRepository;
import com.devsam.shopmanagement.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final CustomerRepository customerRepository;

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    private final OrderItemRepository orderItemRepository;
    // java
    @Override
    @Transactional
    public Order createOrder(OrderRequest orderRequest, User user) {
        if (orderRequest.getCustomerId() == null) throw new IllegalArgumentException("customerId must not be null");
        if (orderRequest.getItems() == null || orderRequest.getItems().isEmpty())
            throw new IllegalArgumentException("items must not be empty");

        var customer = customerRepository.findById(orderRequest.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("customer not found"));

        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus(orderRequest.getStatus() == null ? "PENDING" : orderRequest.getStatus());
        order.setOrderDate(LocalDateTime.now());
        order.setUser(user);

        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemRequest itemRequest : orderRequest.getItems()) {
            var product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order); // owning side
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());

            BigDecimal unitPrice = BigDecimal.valueOf(product.getPrice());
            BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            orderItem.setPriceAtSale(totalPrice);
            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);
        Order savedOrder = orderRepository.save(order);

        return savedOrder;
    }


    @Override
    public Page<OrderResponse> getAllOrders(Pageable pageable, User user) {
        Page<Order> ordersPage = orderRepository.findAllByUser(pageable, user);

        return ordersPage.map(order -> OrderResponse.builder()
                .status(order.getStatus())
                .orderDate(order.getOrderDate())
                .customerName(order.getCustomer() != null ? order.getCustomer().getName() : null)
                .items(order.getOrderItems() != null ? order.getOrderItems().stream()
                        .map(item -> OrderItemResponse.builder()
                                .id(item.getId())
                                .productName(item.getProduct() != null ? item.getProduct().getName() : null)
                                .quantity(item.getQuantity())
                                .priceAtSale(item.getPriceAtSale())
                                .build())
                        .toList() : List.of())
                .build());
    }


    @Override
    public Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId).orElseThrow(()-> new ResourceNotFoundException("order with "+orderId +" not found"));
    }

    @Override
    public Order updateOrder(OrderRequest orderRequest, User user, UUID id) {
// find the existing order
        var orderId = orderRequest.getOrderId();
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order with id " + orderId + " not found"));

        // validate customer
        var customer = customerRepository.findById(orderRequest.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("customer with " + orderRequest.getCustomerId() + " not found"));

        order.setCustomer(customer);
        if (orderRequest.getStatus() != null) {
            order.setStatus(orderRequest.getStatus());
        }
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemRequest itemRequest : orderRequest.getItems()) {
            var product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPriceAtSale(BigDecimal.valueOf(product.getPrice()));
            orderItems.add(orderItem);
        }
        orderItemRepository.saveAll(orderItems);

        // persist and return
        return orderRepository.save(order);
    }

}