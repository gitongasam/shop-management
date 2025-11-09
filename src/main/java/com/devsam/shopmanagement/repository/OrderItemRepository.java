package com.devsam.shopmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.devsam.shopmanagement.entity.OrderItem;

import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
}
