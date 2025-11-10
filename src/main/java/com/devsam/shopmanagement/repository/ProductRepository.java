package com.devsam.shopmanagement.repository;

import com.devsam.shopmanagement.entity.Product;
import com.devsam.shopmanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository  extends JpaRepository<Product, UUID> {

    Page<Product> findByUser(User user, Pageable pageable);
}
