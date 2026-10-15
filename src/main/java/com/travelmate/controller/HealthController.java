package com.travelmate.controller;

import com.travelmate.dto.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@Tag(name = "Health", description = "Service health check endpoint")
public class HealthController {

    @GetMapping
    @Operation(summary = "Check backend and API health status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> healthCheck() {
        Map<String, Object> status = Map.of(
                "status", "UP",
                "service", "TravelMate AI Backend",
                "timestamp", LocalDateTime.now(),
                "version", "1.0.0"
        );
        return ResponseEntity.ok(ApiResponse.success("System operational", status));
    }
}
