package com.devsam.shopmanagement.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String shopName;
    private String verificationCode;
    private LocalDateTime codeExpiredAt;
}