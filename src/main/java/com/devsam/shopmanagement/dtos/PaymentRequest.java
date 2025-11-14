package com.devsam.shopmanagement.dtos;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PaymentRequest {
    private UUID orderId;
    private BigDecimal amount;
    private String paymentMethod;
    private LocalDateTime paymentDate;
}
