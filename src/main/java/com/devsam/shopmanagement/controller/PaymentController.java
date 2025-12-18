package com.devsam.shopmanagement.controller;

import com.devsam.shopmanagement.dtos.PaymentRequest;
import com.devsam.shopmanagement.dtos.PaymentResponse;
import com.devsam.shopmanagement.dtos.SubscriptionRequest;
import com.devsam.shopmanagement.entity.User;
import com.devsam.shopmanagement.repository.PaymentRepository;
import com.devsam.shopmanagement.repository.UserRepository;
import com.devsam.shopmanagement.service.Payment.PaymentService;
import com.devsam.shopmanagement.service.Subscription.SubscriptionService;
import com.sun.security.auth.UserPrincipal;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final SubscriptionService subscriptionService;
    private final UserRepository userRepository;

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

    // In your AuthController/SubscriptionController
    @PostMapping("/subscribe")
    public String subscribe(
            @RequestBody SubscriptionRequest subscriptionRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        // Retrieve the managed User entity from the database
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Pass the request details and the managed user entity to the service
      return subscriptionService.initiateSubscriptionPayment(subscriptionRequest, user);

    }


    @PostMapping("/mpesa/callback")
    public ResponseEntity<?> mpesaCallback(@RequestBody Map<String, Object> payload) {
        try {
            Map body = (Map) payload.get("Body");
            Map stkCallback = (Map) body.get("stkCallback");

            int resultCode = (int) stkCallback.get("ResultCode");

            if (resultCode != 0) {
                System.out.println("Payment failed");
                return ResponseEntity.ok().build();
            }

            Map callbackMetadata = (Map) stkCallback.get("CallbackMetadata");
            List<Map> items = (List<Map>) callbackMetadata.get("Item");

            String receipt = null;
            String phone = null;

            for (Map item : items) {
                if ("MpesaReceiptNumber".equals(item.get("Name"))) {
                    receipt = item.get("Value").toString();
                }
                if ("PhoneNumber".equals(item.get("Name"))) {
                    phone = item.get("Value").toString();
                }
            }

            // find user by phone
            User user = userRepository.findByPhoneNumber(phone)
                    .orElseThrow(() -> new RuntimeException("User not found for phone "));

            // activate subscription
            subscriptionService.activateSubscriptionForUser(
                    user.getId(),
                    30,
                    receipt
            );

            return ResponseEntity.ok("Subscription activated");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().build();
        }
    }
    @GetMapping("/subscription-status")
    public ResponseEntity<?> checkSubscriptionStatus(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isActive = subscriptionService.isActive(user.getId());
        return ResponseEntity.ok(Map.of(
                "subscribed", isActive,
                "status", isActive ? "ACTIVE" : "PENDING_PAYMENT"
        ));
    }
}
