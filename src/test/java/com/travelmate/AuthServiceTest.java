package com.travelmate;

import com.travelmate.dto.auth.AuthResponse;
import com.travelmate.dto.auth.RegisterRequest;
import com.travelmate.entity.User;
import com.travelmate.exception.BadRequestException;
import com.travelmate.repository.UserRepository;
import com.travelmate.security.JwtTokenProvider;
import com.travelmate.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider tokenProvider;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        tokenProvider = new JwtTokenProvider(
                "9a7f8e6d5c4b3a210fedcba9876543210123456789abcdef0123456789abcdef",
                3600000L
        );
        authService = new AuthService(userRepository, passwordEncoder, authenticationManager, tokenProvider);
    }

    @Test
    void testRegisterSuccess() {
        RegisterRequest request = RegisterRequest.builder()
                .name("Alex Johnson")
                .email("alex@example.com")
                .password("secret123")
                .build();

        User savedUser = User.builder()
                .id(1L)
                .name("Alex Johnson")
                .email("alex@example.com")
                .passwordHash(passwordEncoder.encode("secret123"))
                .role("ROLE_USER")
                .build();

        when(userRepository.existsByEmail("alex@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertTrue(response.getToken().length() > 20);
        assertEquals("Alex Johnson", response.getUser().getName());
        assertEquals("alex@example.com", response.getUser().getEmail());
        assertTrue(tokenProvider.validateToken(response.getToken()));
        assertEquals(1L, tokenProvider.getUserIdFromToken(response.getToken()));
    }

    @Test
    void testRegisterDuplicateEmailThrowsBadRequest() {
        RegisterRequest request = RegisterRequest.builder()
                .name("Alex Johnson")
                .email("alex@example.com")
                .password("secret123")
                .build();

        when(userRepository.existsByEmail("alex@example.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register(request));
    }
}
