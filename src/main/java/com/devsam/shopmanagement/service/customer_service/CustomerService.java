package com.devsam.shopmanagement.service.customer_service;

import com.devsam.shopmanagement.dtos.CustomerRequest;
import com.devsam.shopmanagement.entity.Customer;
import com.devsam.shopmanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CustomerService {
    Customer addCustomer(CustomerRequest customerRequest, User user);

    Page<Customer> getAllCustomer(Pageable pageable,User user);

    Customer findCustomerById(UUID id);

    Customer updateCustomer(CustomerRequest customerRequest);
}
