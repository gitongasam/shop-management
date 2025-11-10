package com.devsam.shopmanagement.repository;

import com.devsam.shopmanagement.entity.Customer;
import com.devsam.shopmanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Page<Customer> findByUser(User user, Pageable pageable);
}
