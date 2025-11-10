package com.devsam.shopmanagement.dtos;

import lombok.Data;

@Data
public class CustomerRequest {
    private String name;
    private String location;
    private String phoneNumber;
}
