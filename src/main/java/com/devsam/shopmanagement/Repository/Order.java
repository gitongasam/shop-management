package com.devsam.shopmanagement.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface Order  extends JpaRepository<Order, UUID> {
}
