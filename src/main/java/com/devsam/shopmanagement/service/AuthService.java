package com.devsam.shopmanagement.service;

import com.devsam.shopmanagement.dtos.*;
import com.devsam.shopmanagement.entity.User;
import com.devsam.shopmanagement.repository.UserRepository;
import com.devsam.shopmanagement.security.Jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;

    public RegisterResponse register(RegisterRequest request) {
        // Create new user
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .shopName(request.getShopName())
                .build();
        // Generate verification code
        SecureRandom secureRandom = new SecureRandom();
        String verificationCode = String.format("%06d", secureRandom.nextInt(1_000_000));
        user.setVerificationCode(verificationCode);
        user.setCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        emailService.sendVerificationEmail(request.getFirstName(), request.getEmail(), verificationCode);

        return new RegisterResponse("User created successfully ", user.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

//        check if user is verified

        if (!user.isActive()) {
            throw new RuntimeException("User is not verified please check your email and verify.");
        }

        // Generate new tokens
        String accessToken = jwtService.generateAccessToken(user.getEmail(), "USER");
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        // Update refresh token info
        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiry(LocalDateTime.now().plusDays(7));
        userRepository.save(user);

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refresh(String refreshToken) {
        if (!jwtService.isTokenValid(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String email = jwtService.extractEmail(refreshToken);
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate stored refresh token
        if (!refreshToken.equals(user.getRefreshToken()) ||
                user.getRefreshTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expired or invalid");
        }

        // Generate new access token
        String newAccessToken = jwtService.generateAccessToken(email, "USER");

        return new AuthResponse(newAccessToken, refreshToken);
    }

    public String verifyCode(VerifyRequest verifyRequest) {

        User user = userRepository.findByEmail(verifyRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getVerificationCode().equals(verifyRequest.getCode())
                && user.getCodeExpiresAt().isAfter(LocalDateTime.now())) {
            user.setActive(true);
            user.setVerificationCode(null);
            user.setCodeExpiresAt(null);
            userRepository.save(user);

            return "Email verified successfully proceed to login";
        }
        return "Invalid or expired code";
    }

    public String resendVerificationCode(VerifyRequest verifyRequest) {
        User user = userRepository.findByEmail(verifyRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

//        check if user is active
        if(user.isActive()){
            throw new RuntimeException("User is already verified");
        }

        // Generate verification code
        SecureRandom secureRandom = new SecureRandom();
        String verificationCode = String.format("%06d", secureRandom.nextInt(1_000_000));
        user.setVerificationCode(verificationCode);
        user.setCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        emailService.sendVerificationEmail(verifyRequest.getFirstName(), verifyRequest.getEmail(), verificationCode);

        return "verification code sent succesifully";
    }
}
