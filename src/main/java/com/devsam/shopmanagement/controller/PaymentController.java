package com.devsam.shopmanagement.controller;

import com.devsam.shopmanagement.dtos.PaymentRequest;
import com.devsam.shopmanagement.dtos.PaymentResponse;
import com.devsam.shopmanagement.repository.PaymentRepository;
import com.devsam.shopmanagement.service.Payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public PaymentResponse makePayment(@RequestBody PaymentRequest paymentRequest){
        return paymentService.makePayment(paymentRequest);
    }

    @GetMapping
    public Page<PaymentResponse> getAllPayments(Pageable pageable){
        return paymentService.getAllPayments(pageable);
    }

    @GetMapping("{id}")
    public PaymentResponse getPaymentById(@PathVariable UUID id){
        return paymentService.getPaymentById(id);
    }
}
