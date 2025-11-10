package com.devsam.shopmanagement.controller;

import com.devsam.shopmanagement.dtos.ProductRequest;
import com.devsam.shopmanagement.entity.Product;
import com.devsam.shopmanagement.entity.User;
import com.devsam.shopmanagement.repository.UserRepository;
import com.devsam.shopmanagement.service.product_service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final UserRepository userRepository;

    @PostMapping
    public Product AddProduct(@RequestBody ProductRequest productRequest, @AuthenticationPrincipal UserDetails userDetails) {
       String email = userDetails.getUsername();
       User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return productService.addProduct(productRequest, user);
    }
}
