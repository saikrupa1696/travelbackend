package com.travelmate.service;

import com.travelmate.dto.trip.*;
import com.travelmate.entity.Trip;
import com.travelmate.entity.User;
import com.travelmate.entity.SharedTrip;
import com.travelmate.exception.ResourceNotFoundException;
import com.travelmate.exception.UnauthorizedAccessException;
import com.travelmate.repository.TripRepository;
import com.travelmate.repository.UserRepository;
import com.travelmate.repository.SharedTripRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final SharedTripRepository sharedTripRepository;

    public TripService(TripRepository tripRepository, UserRepository userRepository, SharedTripRepository sharedTripRepository) {
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
        this.sharedTripRepository = sharedTripRepository;
    }

    @Transactional
    public TripResponse createTrip(Long userId, CreateTripRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Trip trip = Trip.builder()
                .user(user)
                .destination(request.getDestination().trim())
                .destinationLatitude(request.getDestinationLatitude())
                .destinationLongitude(request.getDestinationLongitude())
                .destinationPlaceId(request.getDestinationPlaceId())
                .startDate(request.getStartDate())
                .durationDays(request.getDurationDays())
                .travelType(request.getTravelType())
                .travellerCount(request.getTravellerCount())
                .budgetAmount(request.getBudgetAmount())
                .budgetMode(request.getBudgetMode() != null ? request.getBudgetMode().toUpperCase() : "TOTAL")
                .interests(request.getInterests())
                .travelPace(request.getTravelPace() != null ? request.getTravelPace().toUpperCase() : "MODERATE")
                .accommodationPref(request.getAccommodationPref() != null ? request.getAccommodationPref().toUpperCase() : "STANDARD")
                .foodPref(request.getFoodPref() != null ? request.getFoodPref().toUpperCase() : "ANY")
                .additionalNotes(request.getAdditionalNotes())
                .build();

        Trip saved = tripRepository.save(trip);
        return mapToTripResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<TripSummaryResponse> getUserTrips(Long userId, String destination, String travelType) {
        List<Trip> trips;
        if ((destination != null && !destination.isBlank()) || (travelType != null && !travelType.isBlank())) {
            trips = tripRepository.searchTrips(userId, destination, travelType);
        } else {
            trips = tripRepository.findByUserIdOrderByCreatedAtDesc(userId);
        }

        return trips.stream().map(trip -> TripSummaryResponse.builder()
                .id(trip.getId())
                .destination(trip.getDestination())
                .startDate(trip.getStartDate())
                .durationDays(trip.getDurationDays())
                .travelType(trip.getTravelType())
                .travellerCount(trip.getTravellerCount())
                .budgetAmount(trip.getBudgetAmount())
                .budgetMode(trip.getBudgetMode())
                .hasItinerary(trip.getItinerary() != null)
                .generationSource(trip.getItinerary() != null ? trip.getItinerary().getGenerationSource() : null)
                .totalEstimatedCost(trip.getItinerary() != null ? trip.getItinerary().getTotalEstimatedCost() : null)
                .createdAt(trip.getCreatedAt())
                .build()
        ).toList();
    }

    @Transactional(readOnly = true)
    public TripResponse getTripById(Long tripId, Long userId) {
        Trip trip = findTripAndVerifyOwnership(tripId, userId);
        return mapToTripResponse(trip);
    }

    @Transactional
    public TripResponse updateTrip(Long tripId, Long userId, UpdateTripRequest request) {
        Trip trip = findTripAndVerifyOwnership(tripId, userId);

        trip.setDestination(request.getDestination().trim());
        trip.setStartDate(request.getStartDate());
        trip.setDurationDays(request.getDurationDays());
        trip.setTravelType(request.getTravelType());
        trip.setTravellerCount(request.getTravellerCount());
        trip.setBudgetAmount(request.getBudgetAmount());
        trip.setBudgetMode(request.getBudgetMode() != null ? request.getBudgetMode().toUpperCase() : trip.getBudgetMode());
        trip.setInterests(request.getInterests());
        trip.setTravelPace(request.getTravelPace() != null ? request.getTravelPace().toUpperCase() : trip.getTravelPace());
        trip.setAccommodationPref(request.getAccommodationPref() != null ? request.getAccommodationPref().toUpperCase() : trip.getAccommodationPref());
        trip.setFoodPref(request.getFoodPref() != null ? request.getFoodPref().toUpperCase() : trip.getFoodPref());
        trip.setAdditionalNotes(request.getAdditionalNotes());

        Trip saved = tripRepository.save(trip);
        return mapToTripResponse(saved);
    }

    @Transactional
    public void deleteTrip(Long tripId, Long userId) {
        Trip trip = findTripAndVerifyOwnership(tripId, userId);
        tripRepository.delete(trip);
    }

    @Transactional
    public ShareResponse shareTrip(Long tripId, Long userId) {
        Trip trip = findTripAndVerifyOwnership(tripId, userId);

        SharedTrip sharedTrip = sharedTripRepository.findByTripId(tripId)
                .orElseGet(() -> {
                    String token = UUID.randomUUID().toString().replace("-", "");
                    return SharedTrip.builder()
                            .trip(trip)
                            .shareToken(token)
                            .expiresAt(LocalDateTime.now().plusDays(30))
                            .isRevoked(false)
                            .build();
                });

        if (Boolean.TRUE.equals(sharedTrip.getIsRevoked())) {
            sharedTrip.setIsRevoked(false);
            sharedTrip.setExpiresAt(LocalDateTime.now().plusDays(30));
        }

        SharedTrip saved = sharedTripRepository.save(sharedTrip);

        return ShareResponse.builder()
                .shareToken(saved.getShareToken())
                .shareUrl("/shared/" + saved.getShareToken())
                .expiresAt(saved.getExpiresAt())
                .isRevoked(saved.getIsRevoked())
                .build();
    }

    @Transactional(readOnly = true)
    public TripResponse getSharedTrip(String shareToken) {
        SharedTrip sharedTrip = sharedTripRepository.findByShareToken(shareToken)
                .orElseThrow(() -> new ResourceNotFoundException("Shared link invalid or not found"));

        if (Boolean.TRUE.equals(sharedTrip.getIsRevoked())) {
            throw new UnauthorizedAccessException("This shared trip link has been revoked by the owner");
        }

        if (sharedTrip.getExpiresAt() != null && sharedTrip.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedAccessException("This shared trip link has expired");
        }

        return mapToTripResponse(sharedTrip.getTrip());
    }

    @Transactional
    public void revokeShare(Long tripId, Long userId) {
        findTripAndVerifyOwnership(tripId, userId);
        sharedTripRepository.findByTripId(tripId).ifPresent(st -> {
            st.setIsRevoked(true);
            sharedTripRepository.save(st);
        });
    }

    public Trip findTripAndVerifyOwnership(Long tripId, Long userId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", tripId));

        if (!trip.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("You do not have permission to access or modify this trip");
        }

        return trip;
    }

    public TripResponse mapToTripResponse(Trip trip) {
        return TripResponse.builder()
                .id(trip.getId())
                .userId(trip.getUser() != null ? trip.getUser().getId() : null)
                .destination(trip.getDestination())
                .destinationLatitude(trip.getDestinationLatitude())
                .destinationLongitude(trip.getDestinationLongitude())
                .destinationPlaceId(trip.getDestinationPlaceId())
                .startDate(trip.getStartDate())
                .durationDays(trip.getDurationDays())
                .travelType(trip.getTravelType())
                .travellerCount(trip.getTravellerCount())
                .budgetAmount(trip.getBudgetAmount())
                .budgetMode(trip.getBudgetMode())
                .interests(trip.getInterests())
                .travelPace(trip.getTravelPace())
                .accommodationPref(trip.getAccommodationPref())
                .foodPref(trip.getFoodPref())
                .additionalNotes(trip.getAdditionalNotes())
                .createdAt(trip.getCreatedAt())
                .updatedAt(trip.getUpdatedAt())
                .build();
    }
}
