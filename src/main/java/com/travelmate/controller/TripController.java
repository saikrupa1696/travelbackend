package com.travelmate.controller;

import com.travelmate.dto.common.ApiResponse;
import com.travelmate.dto.trip.*;
import com.travelmate.security.UserPrincipal;
import com.travelmate.service.TripService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
@Tag(name = "Trips", description = "Endpoints for creating, managing, and sharing travel plans")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @PostMapping
    @Operation(summary = "Create a new trip")
    public ResponseEntity<ApiResponse<TripResponse>> createTrip(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateTripRequest request) {
        TripResponse response = tripService.createTrip(principal.getId(), request);
        return new ResponseEntity<>(ApiResponse.success("Trip created successfully", response), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all trips for the authenticated user with optional filtering")
    public ResponseEntity<ApiResponse<List<TripSummaryResponse>>> getUserTrips(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) String travelType) {
        List<TripSummaryResponse> response = tripService.getUserTrips(principal.getId(), destination, travelType);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get detailed information for a specific trip")
    public ResponseEntity<ApiResponse<TripResponse>> getTripById(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        TripResponse response = tripService.getTripById(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing trip")
    public ResponseEntity<ApiResponse<TripResponse>> updateTrip(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody UpdateTripRequest request) {
        TripResponse response = tripService.updateTrip(id, principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Trip updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a trip")
    public ResponseEntity<ApiResponse<Void>> deleteTrip(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        tripService.deleteTrip(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Trip deleted successfully", null));
    }

    @PostMapping("/{id}/share")
    @Operation(summary = "Generate or retrieve a public shareable link for a trip")
    public ResponseEntity<ApiResponse<ShareResponse>> shareTrip(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        ShareResponse response = tripService.shareTrip(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Share link generated", response));
    }

    @PostMapping("/{id}/revoke-share")
    @Operation(summary = "Revoke public sharing for a trip")
    public ResponseEntity<ApiResponse<Void>> revokeShare(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        tripService.revokeShare(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Share link revoked successfully", null));
    }
}
