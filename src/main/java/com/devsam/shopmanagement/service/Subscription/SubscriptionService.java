package com.devsam.shopmanagement.service.Subscription;

import com.devsam.shopmanagement.dtos.SubscriptionRequest;
import com.devsam.shopmanagement.entity.Subscription;
import com.devsam.shopmanagement.entity.User;
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
    private static final Map<String, String> PLAN_PRICES = Map.of("monthly", "1000");

    @Transactional
    public String initiateSubscriptionPayment(SubscriptionRequest subscriptionRequest, User user) {

        UUID userId = user.getId();

        // Find or create subscription
        var subscription = subscriptionRepository.findByUser_Id(userId)
                .orElseGet(() -> {
                    Subscription s = Subscription.builder()
                            .user(user)
                            .status("PENDING_PAYMENT")
                            .build();
                    return subscriptionRepository.save(s);
                });

        String plan = subscriptionRequest.getPlan();
        String phoneNumber = subscriptionRequest.getPhoneNumber();

        // Choose amount by plan
        String amount = PLAN_PRICES.getOrDefault(plan, "1");

        // Start STK Push
        String accountRef = "SAMGITONGA-" + user.getId().toString().substring(0, 8);

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
                            .status("ACTIVE")
                            .build();
                });

        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusDays(days));
        subscription.setStatus("ACTIVE");
        subscription.setMpesaTransactionId(mpesaTransactionId);
        subscriptionRepository.save(subscription);
    }

    public boolean isActive(UUID userId) {
        return subscriptionRepository.findByUser_Id(userId)
                .map(s -> s.getStatus().equals("ACTIVE") && s.getEndDate().isAfter(LocalDate.now()))
                .orElse(false);
    }

    @Transactional
    public void expireSubscriptions() {
        subscriptionRepository.findAll().stream()
                .filter(s -> s.getEndDate() != null && s.getEndDate().isBefore(LocalDate.now()) && "ACTIVE".equals(s.getStatus()))
                .forEach(s -> {
                    s.setStatus("EXPIRED");
                    subscriptionRepository.save(s);
                });
    }

}
