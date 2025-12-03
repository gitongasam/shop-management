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
    @Transactional
    public ResponseEntity<String> handleMpesaCallback(@RequestBody Map<String, Object> payload) {
        System.out.println("Mpesa callback received: " + payload);
        try {
            Map body = (Map) payload.get("Body");
            Map stkCallback = (Map) body.get("stkCallback");
            Integer resultCode = (Integer) stkCallback.get("ResultCode");

            if (resultCode == null || resultCode != 0) {
                return ResponseEntity.ok("Payment failed or cancelled");
            }

            // Get callback metadata
            Map callbackMetadata = (Map) stkCallback.get("CallbackMetadata");
            List<Map<String,Object>> items = (List<Map<String,Object>>) callbackMetadata.get("Item");

            String mpesaReceiptNumber = null;
            for (Map<String,Object> item : items) {
                if ("MpesaReceiptNumber".equals(item.get("Name"))) {
                    mpesaReceiptNumber = String.valueOf(item.get("Value"));
                }
            }

            if (mpesaReceiptNumber == null) {
                throw new RuntimeException("Mpesa receipt number not found");
            }

            // Extract the userId from AccountReference (set during STK Push)
            String accountRef = (String) stkCallback.get("AccountReference");
            if (accountRef == null) {
                throw new RuntimeException("AccountReference not found in callback");
            }

            UUID userId = UUID.fromString(accountRef); // parse userId

            // Activate subscription for the logged-in user
            subscriptionService.activateSubscriptionForUser(userId, 30, mpesaReceiptNumber);

            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error processing callback");
        }
    }

}
