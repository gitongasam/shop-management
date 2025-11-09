package com.devsam.shopmanagement.dtos;

import lombok.Data;

@Data
public class ProductRequest {
    private String name;
    private int quantity;
    private double price;
}
