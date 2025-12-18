package com.devsam.shopmanagement.service.Subscription;

import com.devsam.shopmanagement.dtos.SubscriptionRequest;
import com.devsam.shopmanagement.entity.Subscription;
import com.devsam.shopmanagement.entity.User;
import com.devsam.shopmanagement.enums.SubscriptionStatus;
import com.devsam.shopmanagement.repository.SubscriptionRepository;
import com.devsam.shopmanagement.service.Payment.mpesa.MpesaClient;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final com.devsam.shopmanagement.repository.UserRepository userRepository;
    private final MpesaClient mpesaClient;

    // example pricing map
    private static final Map<String, String> PLAN_PRICES = Map.of("monthly", "1");

    @Transactional
    public String initiateSubscriptionPayment(SubscriptionRequest subscriptionRequest, User user) {

        UUID userId = user.getId();
        String plan = subscriptionRequest.getPlan();
        String phoneNumber = subscriptionRequest.getPhoneNumber();

        // Normalize phone number to 254 format
        if (phoneNumber.startsWith("0")) {
            phoneNumber = "254" + phoneNumber.substring(1);
        }

        // Find or create subscription for this user
        Subscription subscription = subscriptionRepository.findByUser_Id(userId)
                .orElseGet(() -> {
                    Subscription s = Subscription.builder()
                            .user(user)
                            .plan(plan)
                            .status(SubscriptionStatus.PENDING_PAYMENT)
                            .build();
                    return subscriptionRepository.save(s);
                });

        // Update plan in case user selected a different one
        subscription.setPlan(plan);
        subscription.setStatus(SubscriptionStatus.PENDING_PAYMENT);
        subscriptionRepository.save(subscription);
        // Choose amount by plan
        String amount = PLAN_PRICES.getOrDefault(plan, "1");
        // Generate account reference for tracking
        String accountRef = "SUB-" + user.getId().toString().substring(0, 8);
        // Start STK Push
        ResponseEntity<Map> resp = mpesaClient.stkPush(
                phoneNumber,
                amount,
                accountRef,
                "Subscription payment"
        );

        Map body = resp.getBody();

        return body != null ? body.toString() : "No STK response";
    }


    @Transactional
    public void activateSubscriptionForUser(UUID userId, int days, String mpesaTransactionId) {
        var subscription = subscriptionRepository.findByUser_Id(userId)
                .orElseGet(() -> {
                    var user = userRepository.findById(userId).orElseThrow();
                    return Subscription.builder()
                            .user(user)
                            .startDate(LocalDate.now())
                            .status(SubscriptionStatus.ACTIVE)
                            .build();
                });

        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusDays(days));
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setMpesaTransactionId(mpesaTransactionId);
        subscriptionRepository.save(subscription);
    }

    public boolean isActive(UUID userId) {
        return subscriptionRepository.findByUser_Id(userId)
                .map(s -> {
                    if (s.getStatus() == null || s.getEndDate() == null) return false;
                    return SubscriptionStatus.ACTIVE.equals(s.getStatus()) && !s.getEndDate().isBefore(LocalDate.now());
                })
                .orElse(false);
    }

    @Transactional
    public void expireSubscriptions() {
        subscriptionRepository.findAll().stream()
                .filter(s -> s.getEndDate() != null && s.getEndDate().isBefore(LocalDate.now()) && SubscriptionStatus.PENDING_PAYMENT.equals(s.getStatus()))
                .forEach(s -> {
                    s.setStatus(SubscriptionStatus.EXPIRED);
                    subscriptionRepository.save(s);
                });
    }

}
