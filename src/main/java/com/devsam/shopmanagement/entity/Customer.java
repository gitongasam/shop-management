package com.devsam.shopmanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    private String phone;

    @Column(name = "total_spent", precision = 12, scale = 2)
    private BigDecimal totalSpent = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties(value = {"applications", "hibernateLazyInitializer"})
    @JsonIgnore
    private User user;

//    consturctor of the dtos
    public Customer(String name, String location, String phoneNumber) {
        this.name = name;
        this.location = location;
        this.phone = phoneNumber;
    }
}

