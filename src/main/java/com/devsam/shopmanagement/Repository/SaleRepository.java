package com.devsam.shopmanagement.Repository;

import com.devsam.shopmanagement.Entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SaleRepository extends JpaRepository<Sale, UUID> {
}
