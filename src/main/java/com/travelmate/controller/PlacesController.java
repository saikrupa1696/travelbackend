package com.travelmate.controller;

import com.travelmate.dto.common.ApiResponse;
import com.travelmate.dto.places.PlaceDto;
import com.travelmate.service.PlacesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/places")
@Tag(name = "Places", description = "Endpoints for discovering real attractions and verified locations")
public class PlacesController {

    private final PlacesService placesService;

    public PlacesController(PlacesService placesService) {
        this.placesService = placesService;
    }

    @GetMapping("/search")
    @Operation(summary = "Search attractions, points of interest, or locations for a destination")
    public ResponseEntity<ApiResponse<List<PlaceDto>>> searchPlaces(
            @RequestParam(name = "destination", required = false) String destination) {
        List<PlaceDto> places = placesService.searchPlaces(destination != null ? destination : "");
        return ResponseEntity.ok(ApiResponse.success(places));
    }

    @GetMapping("/geocode")
    @Operation(summary = "Search structured locations and coordinates for destination autocomplete")
    public ResponseEntity<ApiResponse<List<PlaceDto>>> geocode(
            @RequestParam(name = "query") String query) {
        List<PlaceDto> locations = placesService.geocode(query);
        return ResponseEntity.ok(ApiResponse.success(locations));
    }
}
