package com.devsam.shopmanagement.dtos;


import lombok.Data;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

@Data
public class OrderRequest {

    private UUID orderId;

    private UUID customerId;

    private String status;

    private List<OrderItemRequest> items;

}
