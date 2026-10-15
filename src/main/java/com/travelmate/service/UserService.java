package com.travelmate.service;

import com.travelmate.dto.auth.ChangePasswordRequest;
import com.travelmate.dto.auth.UpdateProfileRequest;
import com.travelmate.dto.auth.UserResponse;
import com.travelmate.entity.User;
import com.travelmate.exception.BadRequestException;
import com.travelmate.exception.ResourceNotFoundException;
import com.travelmate.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthService authService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }

    @Transactional
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        String newEmail = request.getEmail().trim().toLowerCase();
        if (!user.getEmail().equalsIgnoreCase(newEmail) && userRepository.existsByEmail(newEmail)) {
            throw new BadRequestException("Email is already in use by another account");
        }

        user.setName(request.getName().trim());
        user.setEmail(newEmail);
        User saved = userRepository.save(user);

        return authService.mapToUserResponse(saved);
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Current password does not match our records");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
            throw new BadRequestException("New password cannot be the same as the current password");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
