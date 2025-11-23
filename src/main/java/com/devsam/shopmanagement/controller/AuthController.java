package com.devsam.shopmanagement.controller;

import com.devsam.shopmanagement.dtos.*;
import com.devsam.shopmanagement.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public RegisterResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestParam("refreshToken") String refreshToken) {
        return authService.refresh(refreshToken);
    }
    @PostMapping("/verify")
    public String verifyCode(@RequestBody VerifyRequest verifyRequest) {
        return authService.verifyCode(verifyRequest);
    }

    @PostMapping("/resend-verification-code")
    public String resendVerificationCode(@RequestBody VerifyRequest verifyRequest) {
        return authService.resendVerificationCode(verifyRequest);
    }

}
