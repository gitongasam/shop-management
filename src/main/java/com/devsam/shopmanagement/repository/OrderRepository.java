package com.devsam.shopmanagement.repository;

import com.devsam.shopmanagement.entity.Order;
import com.devsam.shopmanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository  extends JpaRepository<Order, UUID> {
    Page<Order> findAllByUser(Pageable pageable, User user);

    UUID id(UUID id);
}
