package com.devsam.shopmanagement.service.Payment;

import com.devsam.shopmanagement.PaymentStatus;
import com.devsam.shopmanagement.dtos.PaymentRequest;
import com.devsam.shopmanagement.dtos.PaymentResponse;
import com.devsam.shopmanagement.entity.Order;
import com.devsam.shopmanagement.entity.Payment;
import com.devsam.shopmanagement.errors.ResourceNotFoundException;
import com.devsam.shopmanagement.repository.OrderRepository;
import com.devsam.shopmanagement.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{


    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public PaymentResponse makePayment(PaymentRequest paymentRequest) {

//         find the order
        Order order = orderRepository.findById(paymentRequest.getOrderId()).orElseThrow(() -> new ResourceNotFoundException("order not found"));

        if(order.getStatus().equals(PaymentStatus.PAID)){
            throw new RuntimeException("Order is already paid");
        }

//        create payment
        Payment payment = Payment.builder()
                .order(order)
                .amount(paymentRequest.getAmount())
                .paymentMethod(paymentRequest.getPaymentMethod())
                .status("SUCCESS")
                .paymentDate(paymentRequest.getPaymentDate())
                .build();

        paymentRepository.save(payment);
//        update order status

        order.setStatus("PAID");
        orderRepository.save(order);

        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(order.getId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .paidAt(payment.getPaymentDate())
                .build();

    }

    @Override
    public Page<PaymentResponse> getAllPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable).map(payment -> PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .paidAt(payment.getPaymentDate())
                .build());
    }

    @Override
    public PaymentResponse getPaymentById(UUID id) {
        return paymentRepository.findById(id).map(payment -> PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .paidAt(payment.getPaymentDate())
                .build()).orElseThrow(()-> new ResourceNotFoundException("payment with id "+ id + " not found"));
    }
}
