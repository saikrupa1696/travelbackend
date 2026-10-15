package com.travelmate.controller;

import com.travelmate.dto.common.ApiResponse;
import com.travelmate.dto.itinerary.ItineraryResponse;
import com.travelmate.dto.itinerary.ModifyItineraryRequest;
import com.travelmate.security.UserPrincipal;
import com.travelmate.service.ItineraryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trips/{id}/itinerary")
@Tag(name = "Itinerary", description = "Endpoints for generating, modifying, and viewing travel itineraries")
public class ItineraryController {

    private final ItineraryService itineraryService;

    public ItineraryController(ItineraryService itineraryService) {
        this.itineraryService = itineraryService;
    }

    @PostMapping("/generate")
    @Operation(summary = "Generate a personalized itinerary using AI with rule-based fallback")
    public ResponseEntity<ApiResponse<ItineraryResponse>> generateItinerary(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        ItineraryResponse response = itineraryService.generateItinerary(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Itinerary generated successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get the current itinerary for a trip")
    public ResponseEntity<ApiResponse<ItineraryResponse>> getItinerary(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        ItineraryResponse response = itineraryService.getItineraryForTrip(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping
    @Operation(summary = "Update or customize itinerary activities manually")
    public ResponseEntity<ApiResponse<ItineraryResponse>> updateItinerary(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @RequestBody ItineraryResponse updateRequest) {
        ItineraryResponse response = itineraryService.updateItinerary(id, principal.getId(), updateRequest);
        return ResponseEntity.ok(ApiResponse.success("Itinerary updated successfully", response));
    }

    @PostMapping("/modify")
    @Operation(summary = "Modify the itinerary using AI prompt instructions")
    public ResponseEntity<ApiResponse<ItineraryResponse>> modifyItinerary(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody ModifyItineraryRequest request) {
        ItineraryResponse response = itineraryService.modifyItinerary(id, principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Itinerary modified successfully", response));
    }

    @PostMapping("/regenerate")
    @Operation(summary = "Regenerate the entire itinerary from scratch")
    public ResponseEntity<ApiResponse<ItineraryResponse>> regenerateItinerary(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        ItineraryResponse response = itineraryService.regenerateItinerary(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Itinerary regenerated successfully", response));
    }
}
