package com.devsam.shopmanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"customers", "products", "orders"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User extends BaseEntity {

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column (nullable = true)
    private String refreshToken;

    @Column(name = "refresh_token_expiry")
    private LocalDateTime refreshTokenExpiry;

    @Column(name = "shop_name", nullable = false)
    private String shopName;

    // Relationships
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Customer> customers;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Product> products;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore // prevents serializing back-reference that can cause recursion
    private List<Order> orders;
}

