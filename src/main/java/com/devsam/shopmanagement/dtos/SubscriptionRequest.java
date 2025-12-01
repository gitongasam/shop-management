package com.devsam.shopmanagement.dtos;

import lombok.Data;

@Data
public class SubscriptionRequest {
    private String phoneNumber;
    private String plan;
}
