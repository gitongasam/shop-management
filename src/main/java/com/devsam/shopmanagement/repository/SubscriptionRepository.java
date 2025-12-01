package com.devsam.shopmanagement.repository;

import com.devsam.shopmanagement.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository  extends JpaRepository<Subscription, UUID> {
    Optional<Subscription> findByUser_Id(UUID userId);
    Optional<Subscription> findByUser_Phone(String phone);
}
