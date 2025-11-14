package com.devsam.shopmanagement.service.Payment;

import com.devsam.shopmanagement.dtos.PaymentRequest;
import com.devsam.shopmanagement.dtos.PaymentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PaymentService {
    PaymentResponse makePayment(PaymentRequest paymentRequest);

    Page<PaymentResponse> getAllPayments(Pageable pageable);

    PaymentResponse getPaymentById(UUID id);

}
