package com.travelmate.service;

import com.travelmate.dto.auth.AuthResponse;
import com.travelmate.dto.auth.LoginRequest;
import com.travelmate.dto.auth.RegisterRequest;
import com.travelmate.dto.auth.UserResponse;
import com.travelmate.entity.User;
import com.travelmate.exception.BadRequestException;
import com.travelmate.repository.UserRepository;
import com.travelmate.security.JwtTokenProvider;
import com.travelmate.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email address is already in use: " + request.getEmail());
        }

        User user = User.builder()
                .name(request.getName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role("ROLE_USER")
                .build();

        User savedUser = userRepository.save(user);

        String token = tokenProvider.generateTokenFromUserId(savedUser.getId(), savedUser.getEmail());

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .user(mapToUserResponse(savedUser))
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().trim().toLowerCase(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new BadRequestException("User record not found"));

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .user(mapToUserResponse(user))
                .build();
    }

    public UserResponse getCurrentUser(UserPrincipal principal) {
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new BadRequestException("User record not found"));
        return mapToUserResponse(user);
    }

    public UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
