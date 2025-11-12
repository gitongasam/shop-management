package com.devsam.shopmanagement.service.order_service;

import com.devsam.shopmanagement.dtos.OrderItemRequest;
import com.devsam.shopmanagement.dtos.OrderRequest;
import com.devsam.shopmanagement.entity.Order;
import com.devsam.shopmanagement.entity.OrderItem;
import com.devsam.shopmanagement.errors.ResourceNotFoundException;
import com.devsam.shopmanagement.repository.CustomerRepository;
import com.devsam.shopmanagement.repository.OrderItemRepository;
import com.devsam.shopmanagement.repository.OrderRepository;
import com.devsam.shopmanagement.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final CustomerRepository customerRepository;

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    private final OrderItemRepository orderItemRepository;
    @Override
    public Order createOrder(OrderRequest orderRequest) {

//         validate customer
        var customer = customerRepository.findById(orderRequest.getCustomerId()).orElseThrow(()-> new ResourceNotFoundException("customer with  found"));

        // 2️⃣ Create order
        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus("PENDING");
        order.setOrderDate(LocalDateTime.now());
        order = orderRepository.save(order);

        // 3️⃣ Add items
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
        return orderRepository.save(order);
    }

    @Override
    public Page<Order> getAllOrders(Pageable pageable, User user) {
        return orderRepository.findAll(pageable);
    }

}
