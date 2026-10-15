package com.travelmate.controller;

import com.travelmate.dto.auth.ChangePasswordRequest;
import com.travelmate.dto.auth.UpdateProfileRequest;
import com.travelmate.dto.auth.UserResponse;
import com.travelmate.dto.common.ApiResponse;
import com.travelmate.security.UserPrincipal;
import com.travelmate.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Endpoints for managing user profile and security settings")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/me")
    @Operation(summary = "Update user name or email")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UpdateProfileRequest request) {
        UserResponse response = userService.updateProfile(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", response));
    }

    @PutMapping("/me/password")
    @Operation(summary = "Change account password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }
}
