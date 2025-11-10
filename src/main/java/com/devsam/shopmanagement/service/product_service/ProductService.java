package com.devsam.shopmanagement.service.product_service;

import com.devsam.shopmanagement.dtos.ProductRequest;
import com.devsam.shopmanagement.entity.Product;
import com.devsam.shopmanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProductService {

//    adding a product
    Product addProduct(ProductRequest  productRequest, User user);

//    get all products
    Page<Product> getAllProducts(Pageable pageable, User user);

//    get product by id
    Product getProductById(UUID id);

//    Update product
    Product updateProductById(UUID id, ProductRequest productRequest);
//    delete product by Id
}
