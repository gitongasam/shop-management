package com.devsam.shopmanagement.Repository;

import com.devsam.shopmanagement.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
