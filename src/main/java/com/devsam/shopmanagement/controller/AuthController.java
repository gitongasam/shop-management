package com.devsam.shopmanagement.controller;

import com.devsam.shopmanagement.dtos.AuthResponse;
import com.devsam.shopmanagement.dtos.LoginRequest;
import com.devsam.shopmanagement.dtos.RegisterRequest;
import com.devsam.shopmanagement.dtos.RegisterResponse;
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

//    @PostMapping("forgot-password")
//    public void forgotPassword(@RequestParam("email") String email) {
//        authService.forgotPassword(email);
//    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestParam("refreshToken") String refreshToken) {
        return authService.refresh(refreshToken);
    }
}
