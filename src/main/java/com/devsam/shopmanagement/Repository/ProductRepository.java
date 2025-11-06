package com.devsam.shopmanagement.Repository;

import com.devsam.shopmanagement.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository  extends JpaRepository<Product, UUID> {
}
