package com.devsam.shopmanagement.dtos;

import com.devsam.shopmanagement.entity.BaseEntity;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrderResponse  extends BaseEntity {
    private String status;
    private LocalDateTime orderDate;
    private String customerName;
    private List<OrderItemResponse> items;
}