package com.devsam.shopmanagement.controller;

import com.devsam.shopmanagement.entity.Customer;
import com.devsam.shopmanagement.entity.User;
import com.devsam.shopmanagement.repository.CustomerRepository;
import com.devsam.shopmanagement.repository.UserRepository;
import com.devsam.shopmanagement.service.customer_service.CustomerService;
import com.sun.security.auth.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.devsam.shopmanagement.dtos.CustomerRequest;

import java.util.UUID;

@RequestMapping("api/v1/customers")
@RestController
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final UserRepository userRepository;
//    add customer

    @PostMapping
    public Customer addCustomer(@RequestBody CustomerRequest customerRequest,
                                @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return customerService.addCustomer(customerRequest, user);

    }
//    get all customers

    @GetMapping
    public Page<Customer> getAllCustomers(Pageable pageable, @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();

        User user = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found"));
        return customerService.getAllCustomer(pageable,user);
    }
//    get customer by id

    @GetMapping("{id}")
    public Customer getCustomer(@RequestParam UUID id) {
        return customerService.findCustomerById(id);
    }

    //    update customer
    @PutMapping("{id}")
    public Customer updateCustomer(@PathVariable UUID id, @RequestBody CustomerRequest customerRequest) {
        return updateCustomer(id, customerRequest);
    }
//    delete customer by id



}
