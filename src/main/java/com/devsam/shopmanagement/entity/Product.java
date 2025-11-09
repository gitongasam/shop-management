package com.devsam.shopmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private double price;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Product(String name, int quantity, double price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }
}
