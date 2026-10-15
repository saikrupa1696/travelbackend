package com.travelmate.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelmate.dto.itinerary.ActivityResponse;
import com.travelmate.dto.itinerary.BudgetBreakdownResponse;
import com.travelmate.dto.itinerary.DayResponse;
import com.travelmate.dto.itinerary.ItineraryResponse;
import com.travelmate.dto.itinerary.ModifyItineraryRequest;
import com.travelmate.entity.*;
import com.travelmate.exception.ResourceNotFoundException;
import com.travelmate.repository.ItineraryActivityRepository;
import com.travelmate.repository.ItineraryDayRepository;
import com.travelmate.repository.ItineraryRepository;
import com.travelmate.service.ai.ItineraryGenerationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ItineraryService {

    private static final Logger log = LoggerFactory.getLogger(ItineraryService.class);

    private final ItineraryRepository itineraryRepository;
    private final ItineraryDayRepository dayRepository;
    private final ItineraryActivityRepository activityRepository;
    private final TripService tripService;
    private final ItineraryGenerationService itineraryGenerationService;
    private final BudgetCalculationService budgetCalculationService;
    private final ObjectMapper objectMapper;

    public ItineraryService(ItineraryRepository itineraryRepository,
                            ItineraryDayRepository dayRepository,
                            ItineraryActivityRepository activityRepository,
                            TripService tripService,
                            ItineraryGenerationService itineraryGenerationService,
                            BudgetCalculationService budgetCalculationService,
                            ObjectMapper objectMapper) {
        this.itineraryRepository = itineraryRepository;
        this.dayRepository = dayRepository;
        this.activityRepository = activityRepository;
        this.tripService = tripService;
        this.itineraryGenerationService = itineraryGenerationService;
        this.budgetCalculationService = budgetCalculationService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public ItineraryResponse generateItinerary(Long tripId, Long userId) {
        Trip trip = tripService.findTripAndVerifyOwnership(tripId, userId);

        ItineraryResponse generated = itineraryGenerationService.generateItinerary(trip);

        itineraryRepository.findByTripId(tripId).ifPresent(itin -> {
            itineraryRepository.delete(itin);
            itineraryRepository.flush();
        });

        return persistItineraryData(trip, generated);
    }

    @Transactional(readOnly = true)
    public ItineraryResponse getItineraryForTrip(Long tripId, Long userId) {
        if (userId != null) {
            tripService.findTripAndVerifyOwnership(tripId, userId);
        }

        Itinerary itinerary = itineraryRepository.findByTripId(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Itinerary not found for trip: " + tripId));

        return mapEntityToResponse(itinerary);
    }

    @Transactional
    public ItineraryResponse modifyItinerary(Long tripId, Long userId, ModifyItineraryRequest request) {
        Trip trip = tripService.findTripAndVerifyOwnership(tripId, userId);
        Itinerary existing = itineraryRepository.findByTripId(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Itinerary not found for trip: " + tripId));

        ItineraryResponse modified = itineraryGenerationService.modifyItinerary(trip, existing, request);

        itineraryRepository.delete(existing);
        itineraryRepository.flush();
        return persistItineraryData(trip, modified);
    }

    @Transactional
    public ItineraryResponse regenerateItinerary(Long tripId, Long userId) {
        return generateItinerary(tripId, userId);
    }

    @Transactional
    public ItineraryResponse updateItinerary(Long tripId, Long userId, ItineraryResponse updateRequest) {
        Trip trip = tripService.findTripAndVerifyOwnership(tripId, userId);
        Itinerary existing = itineraryRepository.findByTripId(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Itinerary not found for trip: " + tripId));

        itineraryRepository.delete(existing);
        itineraryRepository.flush();
        return persistItineraryData(trip, updateRequest);
    }

    private ItineraryResponse persistItineraryData(Trip trip, ItineraryResponse data) {
        try {
            String jsonSnapshot = objectMapper.writeValueAsString(data);

            Itinerary itinerary = Itinerary.builder()
                    .trip(trip)
                    .title(data.getTitle())
                    .summary(data.getSummary())
                    .generationSource(data.getGenerationSource())
                    .totalEstimatedCost(data.getTotalEstimatedCost())
                    .remainingBudget(data.getRemainingBudget())
                    .budgetStatus(data.getBudgetStatus())
                    .itineraryData(jsonSnapshot)
                    .days(new ArrayList<>())
                    .build();

            Itinerary savedItinerary = itineraryRepository.save(itinerary);

            BigDecimal totalActivityCosts = BigDecimal.ZERO;

            if (data.getDays() != null) {
                for (DayResponse dayDto : data.getDays()) {
                    ItineraryDay day = ItineraryDay.builder()
                            .itinerary(savedItinerary)
                            .dayNumber(dayDto.getDayNumber())
                            .date(dayDto.getDate())
                            .theme(dayDto.getTheme())
                            .dailyBudget(dayDto.getDailyBudget())
                            .dailyEstimatedCost(dayDto.getDailyEstimatedCost())
                            .activities(new ArrayList<>())
                            .build();

                    ItineraryDay savedDay = dayRepository.save(day);

                    if (dayDto.getActivities() != null) {
                        for (ActivityResponse actDto : dayDto.getActivities()) {
                            ItineraryActivity act = ItineraryActivity.builder()
                                    .itineraryDay(savedDay)
                                    .sequenceNumber(actDto.getSequenceNumber())
                                    .placeName(actDto.getPlaceName())
                                    .category(actDto.getCategory())
                                    .description(actDto.getDescription())
                                    .startTime(actDto.getStartTime())
                                    .endTime(actDto.getEndTime())
                                    .durationMinutes(actDto.getDurationMinutes())
                                    .estimatedCost(actDto.getEstimatedCost())
                                    .costType(actDto.getCostType())
                                    .placeReference(actDto.getPlaceReference())
                                    .photoUrl(actDto.getPhotoUrl())
                                    .address(actDto.getAddress())
                                    .rating(actDto.getRating())
                                    .openingHours(actDto.getOpeningHours())
                                    .latitude(actDto.getLatitude())
                                    .longitude(actDto.getLongitude())
                                    .build();

                            activityRepository.save(act);

                            if (act.getEstimatedCost() != null) {
                                totalActivityCosts = totalActivityCosts.add(act.getEstimatedCost());
                            }
                        }
                    }
                }
            }

            return mapEntityToResponse(savedItinerary);
        } catch (Exception ex) {
            log.error("Failed to persist itinerary", ex);
            throw new RuntimeException("Could not persist itinerary: " + ex.getMessage());
        }
    }

    public ItineraryResponse mapEntityToResponse(Itinerary itinerary) {
        Trip trip = itinerary.getTrip();
        List<ItineraryDay> daysList = dayRepository.findByItineraryIdOrderByDayNumberAsc(itinerary.getId());

        List<DayResponse> dayResponses = new ArrayList<>();
        BigDecimal totalActivitiesCost = BigDecimal.ZERO;

        for (ItineraryDay day : daysList) {
            List<ItineraryActivity> acts = activityRepository.findByItineraryDayIdOrderBySequenceNumberAsc(day.getId());
            List<ActivityResponse> actResponses = new ArrayList<>();

            for (ItineraryActivity act : acts) {
                if (act.getEstimatedCost() != null) {
                    totalActivitiesCost = totalActivitiesCost.add(act.getEstimatedCost());
                }

                actResponses.add(ActivityResponse.builder()
                        .id(act.getId())
                        .sequenceNumber(act.getSequenceNumber())
                        .placeName(act.getPlaceName())
                        .category(act.getCategory())
                        .description(act.getDescription())
                        .startTime(act.getStartTime())
                        .endTime(act.getEndTime())
                        .durationMinutes(act.getDurationMinutes())
                        .estimatedCost(act.getEstimatedCost())
                        .costType(act.getCostType())
                        .placeReference(act.getPlaceReference())
                        .photoUrl(act.getPhotoUrl())
                        .address(act.getAddress())
                        .rating(act.getRating())
                        .openingHours(act.getOpeningHours())
                        .latitude(act.getLatitude())
                        .longitude(act.getLongitude())
                        .build());
            }

            dayResponses.add(DayResponse.builder()
                    .id(day.getId())
                    .dayNumber(day.getDayNumber())
                    .date(day.getDate())
                    .theme(day.getTheme())
                    .dailyBudget(day.getDailyBudget())
                    .dailyEstimatedCost(day.getDailyEstimatedCost())
                    .activities(actResponses)
                    .build());
        }

        BudgetBreakdownResponse budgetBreakdown = budgetCalculationService.calculateBudgetBreakdown(trip, totalActivitiesCost);

        return ItineraryResponse.builder()
                .id(itinerary.getId())
                .tripId(trip.getId())
                .destination(trip.getDestination())
                .title(itinerary.getTitle())
                .summary(itinerary.getSummary())
                .generationSource(itinerary.getGenerationSource())
                .totalEstimatedCost(budgetBreakdown.getTotalEstimatedCost())
                .remainingBudget(budgetBreakdown.getRemainingBudget())
                .budgetStatus(budgetBreakdown.getBudgetStatus())
                .days(dayResponses)
                .budgetBreakdown(budgetBreakdown)
                .createdAt(itinerary.getCreatedAt())
                .updatedAt(itinerary.getUpdatedAt())
                .build();
    }
}
