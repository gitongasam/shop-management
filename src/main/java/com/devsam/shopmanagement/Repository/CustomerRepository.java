package com.devsam.shopmanagement.Repository;

import com.devsam.shopmanagement.Entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
}
