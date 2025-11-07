package com.devsam.shopmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface Order  extends JpaRepository<Order, UUID> {
}
