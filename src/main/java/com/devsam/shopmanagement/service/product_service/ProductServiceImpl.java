package com.devsam.shopmanagement.service.product_service;


import com.devsam.shopmanagement.dtos.ProductRequest;
import com.devsam.shopmanagement.entity.Customer;
import com.devsam.shopmanagement.entity.Product;
import com.devsam.shopmanagement.entity.User;
import com.devsam.shopmanagement.errors.ResourceNotFoundException;
import com.devsam.shopmanagement.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    @Override
    public Product addProduct(ProductRequest productRequest, User user) {
         Product product = Product.builder()
                 .name(productRequest.getName())
                 .price(productRequest.getPrice())
                 .quantity(productRequest.getQuantity())
                 .user(user)
                 .build();
        System.out.println("price: " + productRequest.getPrice());

        return productRepository.save(product);

    }


    @Override
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public Product getProductById(UUID id) {
        return productRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException(" product with id "+ id + "not found"));
    }

    @Override
    public Product updateProductById(UUID id, ProductRequest productRequest) {
        return null;
    }
}
