package com.devsam.shopmanagement.service.customer_service;

import com.devsam.shopmanagement.dtos.CustomerRequest;
import com.devsam.shopmanagement.entity.Customer;
import com.devsam.shopmanagement.errors.ResourceNotFoundException;
import com.devsam.shopmanagement.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    //    add customer
    @Override
    public Customer addCustomer(CustomerRequest customerRequest) {

        Customer customer = new Customer(
                customerRequest.getName(),
                customerRequest.getEmail(),
                customerRequest.getPhoneNumber()
        );
        return customerRepository.save(customer);
    }

    @Override
    public Page<Customer> getAllCustomer(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

    @Override
    public Customer findCustomerById(UUID id) {
        return customerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("customer with " + id + " not found"));
    }

    @Override
    public Customer updateCustomer(CustomerRequest customerRequest) {
        return null;
    }
}
