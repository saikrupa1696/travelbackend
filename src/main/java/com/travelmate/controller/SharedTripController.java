package com.travelmate.controller;

import com.travelmate.dto.common.ApiResponse;
import com.travelmate.dto.itinerary.ItineraryResponse;
import com.travelmate.dto.trip.TripResponse;
import com.travelmate.service.ItineraryService;
import com.travelmate.service.TripService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shared")
@Tag(name = "Shared Trips", description = "Public endpoints for accessing shared itineraries without login")
public class SharedTripController {

    private final TripService tripService;
    private final ItineraryService itineraryService;

    public SharedTripController(TripService tripService, ItineraryService itineraryService) {
        this.tripService = tripService;
        this.itineraryService = itineraryService;
    }

    @GetMapping("/{token}")
    @Operation(summary = "Get trip overview by share token")
    public ResponseEntity<ApiResponse<TripResponse>> getSharedTrip(@PathVariable String token) {
        TripResponse trip = tripService.getSharedTrip(token);
        return ResponseEntity.ok(ApiResponse.success(trip));
    }

    @GetMapping("/{token}/itinerary")
    @Operation(summary = "Get full itinerary for a shared trip by token")
    public ResponseEntity<ApiResponse<ItineraryResponse>> getSharedItinerary(@PathVariable String token) {
        TripResponse trip = tripService.getSharedTrip(token);
        ItineraryResponse itinerary = itineraryService.getItineraryForTrip(trip.getId(), null);
        return ResponseEntity.ok(ApiResponse.success(itinerary));
    }
}
