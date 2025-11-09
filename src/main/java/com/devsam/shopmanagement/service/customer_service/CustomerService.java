package com.devsam.shopmanagement.service.customer_service;

import com.devsam.shopmanagement.dtos.CustomerRequest;
import com.devsam.shopmanagement.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CustomerService {
    Customer addCustomer(CustomerRequest customerRequest);

    Page<Customer> getAllCustomer(Pageable pageable);

    Customer findCustomerById(UUID id);

    Customer updateCustomer(CustomerRequest customerRequest);
}
