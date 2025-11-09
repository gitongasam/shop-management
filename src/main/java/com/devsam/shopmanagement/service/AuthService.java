package com.devsam.shopmanagement.service;

import com.devsam.shopmanagement.dtos.AuthResponse;
import com.devsam.shopmanagement.dtos.LoginRequest;
import com.devsam.shopmanagement.dtos.RegisterRequest;
import com.devsam.shopmanagement.entity.User;
import com.devsam.shopmanagement.repository.UserRepository;
import com.devsam.shopmanagement.security.Jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        // Create new user
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .shopName(request.getShopName())
                .build();

        // Save user before generating tokens (so they have an ID)
        userRepository.save(user);

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(user.getEmail(), "USER");
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        // Save refresh token
        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiry(LocalDateTime.now().plusDays(7));
        userRepository.save(user);

        System.out.println(user);

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse login(LoginRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
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
}
