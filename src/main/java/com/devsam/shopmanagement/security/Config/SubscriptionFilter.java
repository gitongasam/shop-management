package com.devsam.shopmanagement.security.Config;

import com.devsam.shopmanagement.entity.User;
import com.devsam.shopmanagement.repository.UserRepository;
import com.devsam.shopmanagement.service.Subscription.SubscriptionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Component
public class SubscriptionFilter extends OncePerRequestFilter {

    private final SubscriptionService subscriptionService;
    private final UserRepository userRepository;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    // paths that must be reachable without an active subscription
    private final List<String> whitelist = List.of(
            "/api/v1/payments/subscribe",
            "/api/v1/payments/subscription-status",
            "/api/v1/payments/mpesa/callback",
            "/api/v1/auth/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    );

    public SubscriptionFilter(SubscriptionService subscriptionService, UserRepository userRepository) {
        this.subscriptionService = subscriptionService;
        this.userRepository = userRepository;
    }

    private boolean isWhitelisted(HttpServletRequest request) {
        String path = request.getRequestURI();
        return whitelist.stream().anyMatch(p -> pathMatcher.match(p, path)) || "OPTIONS".equalsIgnoreCase(request.getMethod());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (isWhitelisted(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            // let other security filters handle unauthenticated requests
            filterChain.doFilter(request, response);
            return;
        }

        String username = null;
        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            username = (String) principal;
        }

        if (username == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Optional<User> optUser = userRepository.findByEmail(username);
        if (optUser.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        User user = optUser.get();
        boolean active = subscriptionService.isActive(user.getId());
        if (active) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(402); // Payment Required
            response.setContentType("application/json");
            String msg = "{\"error\":\"subscription_required\",\"message\":\"Subscription required to access this resource.\"}";
            response.getOutputStream().write(msg.getBytes(StandardCharsets.UTF_8));
        }
    }
}
