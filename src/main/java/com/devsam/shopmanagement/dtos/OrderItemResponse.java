package com.devsam.shopmanagement.dtos;

import com.devsam.shopmanagement.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse extends BaseEntity {
    private UUID id;
    private String productName;
    private int quantity;
    private BigDecimal priceAtSale;
}
