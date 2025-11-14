package com.devsam.shopmanagement.dtos;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PaymentResponse {
    private UUID id;
    private UUID orderId;
    private BigDecimal amount;
    private String paymentMethod;
    private String status;
    private String transactionId;
    private LocalDateTime paidAt;
}
