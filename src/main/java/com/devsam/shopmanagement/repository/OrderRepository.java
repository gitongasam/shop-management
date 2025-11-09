package com.devsam.shopmanagement.repository;

import com.devsam.shopmanagement.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository  extends JpaRepository<Order, UUID> {
}
