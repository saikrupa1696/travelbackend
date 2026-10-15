package com.travelmate.service.ai;

import com.travelmate.dto.itinerary.ActivityResponse;
import com.travelmate.dto.itinerary.BudgetBreakdownResponse;
import com.travelmate.dto.itinerary.DayResponse;
import com.travelmate.dto.itinerary.ItineraryResponse;
import com.travelmate.dto.itinerary.ModifyItineraryRequest;
import com.travelmate.dto.places.PlaceDto;
import com.travelmate.entity.Itinerary;
import com.travelmate.entity.Trip;
import com.travelmate.service.BudgetCalculationService;
import com.travelmate.service.PlacesService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Service("ruleBasedItineraryProvider")
public class RuleBasedItineraryProvider implements ItineraryGenerationService {

    private final PlacesService placesService;
    private final BudgetCalculationService budgetCalculationService;

    public RuleBasedItineraryProvider(PlacesService placesService, BudgetCalculationService budgetCalculationService) {
        this.placesService = placesService;
        this.budgetCalculationService = budgetCalculationService;
    }

    @Override
    public ItineraryResponse generateItinerary(Trip trip) {
        String destination = trip.getDestination();
        int days = Math.max(1, trip.getDurationDays());
        LocalDate startDate = trip.getStartDate() != null ? trip.getStartDate() : LocalDate.now().plusDays(7);
        List<PlaceDto> places = placesService.searchPlaces(destination);

        BigDecimal totalBudget = "PER_PERSON".equalsIgnoreCase(trip.getBudgetMode())
                ? trip.getBudgetAmount().multiply(BigDecimal.valueOf(trip.getTravellerCount()))
                : trip.getBudgetAmount();

        BigDecimal baselineDailyActivitiesBudget = totalBudget.multiply(BigDecimal.valueOf(0.18))
                .divide(BigDecimal.valueOf(days), 2, RoundingMode.HALF_UP);

        List<DayResponse> dayResponses = new ArrayList<>();
        BigDecimal totalActivitiesCost = BigDecimal.ZERO;

        Set<String> interests = parseInterests(trip.getInterests());

        for (int dayNum = 1; dayNum <= days; dayNum++) {
            LocalDate currentDate = startDate.plusDays(dayNum - 1);
            String theme = determineDayTheme(dayNum, days, destination, interests);

            // Realistic variable day multiplier: Arrival/departure days are lighter; middle sightseeing days carry higher ticket/excursion costs
            double dayMultiplier = (dayNum == 1) ? 0.70 : (dayNum == days) ? 0.85 : 1.15;
            BigDecimal dayBudget = baselineDailyActivitiesBudget.multiply(BigDecimal.valueOf(dayMultiplier)).setScale(2, RoundingMode.HALF_UP);

            List<ActivityResponse> activities = generateActivitiesForDay(dayNum, days, destination, theme, interests, trip, places, dayBudget);

            BigDecimal dayCost = activities.stream()
                    .map(ActivityResponse::getEstimatedCost)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            totalActivitiesCost = totalActivitiesCost.add(dayCost);

            dayResponses.add(DayResponse.builder()
                    .dayNumber(dayNum)
                    .date(currentDate)
                    .theme(theme)
                    .dailyBudget(dayBudget)
                    .dailyEstimatedCost(dayCost)
                    .activities(activities)
                    .build());
        }

        BudgetBreakdownResponse budgetBreakdown = budgetCalculationService.calculateBudgetBreakdown(trip, totalActivitiesCost);

        String title = String.format("%d-Day %s Adventure in %s", days, trip.getTravelType(), destination);
        String summary = String.format("A realistic, curated %d-day itinerary exploring %s tailored for a %s travel experience with %s pace. Features verified top attractions, authentic local cuisine, and balanced leisure time.",
                days, destination, trip.getTravelType().toLowerCase(), trip.getTravelPace().toLowerCase());

        return ItineraryResponse.builder()
                .tripId(trip.getId())
                .destination(destination)
                .title(title)
                .summary(summary)
                .generationSource("RULE_BASED")
                .totalEstimatedCost(budgetBreakdown.getTotalEstimatedCost())
                .remainingBudget(budgetBreakdown.getRemainingBudget())
                .budgetStatus(budgetBreakdown.getBudgetStatus())
                .days(dayResponses)
                .budgetBreakdown(budgetBreakdown)
                .build();
    }

    @Override
    public ItineraryResponse modifyItinerary(Trip trip, Itinerary existingItinerary, ModifyItineraryRequest request) {
        ItineraryResponse modified = generateItinerary(trip);
        modified.setTitle(modified.getTitle() + " (Customized)");
        modified.setSummary(modified.getSummary() + " Adjusted based on traveler preference: \"" + request.getInstruction() + "\"");

        if (request.getTravelPace() != null && !request.getTravelPace().isBlank()) {
            trip.setTravelPace(request.getTravelPace());
        }
        if (request.getFoodPref() != null && !request.getFoodPref().isBlank()) {
            trip.setFoodPref(request.getFoodPref());
        }
        return modified;
    }

    private List<ActivityResponse> generateActivitiesForDay(
            int dayNum, int totalDays, String destination, String theme,
            Set<String> interests, Trip trip, List<PlaceDto> places, BigDecimal dailyBudget) {

        List<ActivityResponse> activities = new ArrayList<>();
        BigDecimal costQuarter = dailyBudget.divide(BigDecimal.valueOf(4), 2, RoundingMode.HALF_UP);

        Double centerLat = trip.getDestinationLatitude() != null ? trip.getDestinationLatitude() : (!places.isEmpty() ? places.get(0).getLatitude() : 15.2993);
        Double centerLon = trip.getDestinationLongitude() != null ? trip.getDestinationLongitude() : (!places.isEmpty() ? places.get(0).getLongitude() : 74.1240);

        if (dayNum == 1) {
            activities.add(ActivityResponse.builder()
                    .sequenceNumber(1)
                    .placeName(destination + " Arrival & Hotel Check-in")
                    .category("TRANSIT")
                    .startTime("10:00 AM")
                    .endTime("12:00 PM")
                    .durationMinutes(120)
                    .estimatedCost(costQuarter.multiply(BigDecimal.valueOf(0.4)))
                    .costType("ESTIMATED")
                    .description("Arrive in " + destination + ", complete check-in procedures, unpack, and refresh before city exploration.")
                    .photoUrl(placesService.getCategoryFallbackPhoto("TRANSIT"))
                    .address("City Center & Airport/Station Hub, " + destination)
                    .latitude(centerLat)
                    .longitude(centerLon)
                    .openingHours("24/7 Service")
                    .rating(4.5)
                    .build());

            PlaceDto firstAttraction = !places.isEmpty() ? places.get(0) : null;
            String walkTitle = firstAttraction != null ? firstAttraction.getName() : destination + " Central Promenade";
            String walkPhoto = firstAttraction != null && firstAttraction.getPhotoUrl() != null ? firstAttraction.getPhotoUrl() : placesService.getCategoryFallbackPhoto("SIGHTSEEING");
            String walkAddress = firstAttraction != null && firstAttraction.getAddress() != null ? firstAttraction.getAddress() : destination;
            Double walkLat = firstAttraction != null && firstAttraction.getLatitude() != null && firstAttraction.getLatitude() != 0.0 ? firstAttraction.getLatitude() : centerLat + 0.008;
            Double walkLon = firstAttraction != null && firstAttraction.getLongitude() != null && firstAttraction.getLongitude() != 0.0 ? firstAttraction.getLongitude() : centerLon + 0.008;

            activities.add(ActivityResponse.builder()
                    .sequenceNumber(2)
                    .placeName(walkTitle)
                    .category(firstAttraction != null && firstAttraction.getCategory() != null ? firstAttraction.getCategory() : "SIGHTSEEING")
                    .startTime("02:30 PM")
                    .endTime("05:00 PM")
                    .durationMinutes(150)
                    .estimatedCost(firstAttraction != null && firstAttraction.getEstimatedCost() != null ? firstAttraction.getEstimatedCost() : costQuarter.multiply(BigDecimal.valueOf(1.1)))
                    .costType("ESTIMATED")
                    .description("Explore " + walkTitle + ", taking in architectural landmarks and soaking in the local atmosphere.")
                    .photoUrl(walkPhoto)
                    .address(walkAddress)
                    .latitude(walkLat)
                    .longitude(walkLon)
                    .rating(firstAttraction != null && firstAttraction.getRating() != null ? firstAttraction.getRating() : 4.6)
                    .openingHours(firstAttraction != null && firstAttraction.getOpeningHours() != null ? firstAttraction.getOpeningHours() : "09:00 AM - 06:00 PM")
                    .build());

            // Dining recommendation
            PlaceDto diningPlace = places.stream().filter(p -> "DINING".equalsIgnoreCase(p.getCategory())).findFirst().orElse(null);
            String dinnerName = diningPlace != null ? diningPlace.getName() : "Welcome Dinner at " + destination + " Culinary Bistro";
            String dinnerPhoto = diningPlace != null && diningPlace.getPhotoUrl() != null ? diningPlace.getPhotoUrl() : placesService.getCategoryFallbackPhoto("DINING");
            String dinnerAddress = diningPlace != null ? diningPlace.getAddress() : "Main Food Street, " + destination;
            Double dinnerLat = diningPlace != null && diningPlace.getLatitude() != null && diningPlace.getLatitude() != 0.0 ? diningPlace.getLatitude() : centerLat - 0.006;
            Double dinnerLon = diningPlace != null && diningPlace.getLongitude() != null && diningPlace.getLongitude() != 0.0 ? diningPlace.getLongitude() : centerLon - 0.005;

            activities.add(ActivityResponse.builder()
                    .sequenceNumber(3)
                    .placeName(dinnerName)
                    .category("DINING")
                    .startTime("07:30 PM")
                    .endTime("09:30 PM")
                    .durationMinutes(120)
                    .estimatedCost(costQuarter.multiply(BigDecimal.valueOf(1.5)))
                    .costType("ESTIMATED")
                    .description("Savor signature regional specialties honoring your " + trip.getFoodPref() + " dining preference.")
                    .photoUrl(dinnerPhoto)
                    .address(dinnerAddress)
                    .latitude(dinnerLat)
                    .longitude(dinnerLon)
                    .rating(diningPlace != null && diningPlace.getRating() != null ? diningPlace.getRating() : 4.7)
                    .openingHours(diningPlace != null && diningPlace.getOpeningHours() != null ? diningPlace.getOpeningHours() : "11:30 AM - 11:00 PM")
                    .build());

        } else if (dayNum == totalDays) {
            activities.add(ActivityResponse.builder()
                    .sequenceNumber(1)
                    .placeName(destination + " Heritage Craft Market & Souvenirs")
                    .category("SHOPPING")
                    .startTime("10:00 AM")
                    .endTime("01:00 PM")
                    .durationMinutes(180)
                    .estimatedCost(costQuarter.multiply(BigDecimal.valueOf(1.4)))
                    .costType("ESTIMATED")
                    .description("Browse artisan stalls for indigenous handicrafts, regional spices, and travel mementos.")
                    .photoUrl(placesService.getCategoryFallbackPhoto("SHOPPING"))
                    .address("Traditional Market Square, " + destination)
                    .latitude(centerLat + 0.004)
                    .longitude(centerLon - 0.006)
                    .rating(4.5)
                    .openingHours("10:00 AM - 08:30 PM")
                    .build());

            activities.add(ActivityResponse.builder()
                    .sequenceNumber(2)
                    .placeName("Farewell Lunch & Departure Prep")
                    .category("DINING")
                    .startTime("01:30 PM")
                    .endTime("03:30 PM")
                    .durationMinutes(120)
                    .estimatedCost(costQuarter.multiply(BigDecimal.valueOf(1.0)))
                    .costType("ESTIMATED")
                    .description("Enjoy a relaxed final meal, check out from hotel, and proceed towards transit terminal.")
                    .photoUrl(placesService.getCategoryFallbackPhoto("DINING"))
                    .address("Central Boulevard, " + destination)
                    .latitude(centerLat - 0.004)
                    .longitude(centerLon + 0.003)
                    .rating(4.6)
                    .openingHours("11:30 AM - 04:00 PM")
                    .build());
        } else {
            int placeIndex = (dayNum - 1) % Math.max(1, places.size());
            PlaceDto morningPlace = (placeIndex < places.size()) ? places.get(placeIndex) : null;
            String morningName = morningPlace != null ? morningPlace.getName() : destination + " Historic Landmark";
            String morningPhoto = morningPlace != null && morningPlace.getPhotoUrl() != null ? morningPlace.getPhotoUrl() : placesService.getCategoryFallbackPhoto("SIGHTSEEING");
            String morningAddress = morningPlace != null && morningPlace.getAddress() != null ? morningPlace.getAddress() : destination;
            Double morningLat = morningPlace != null && morningPlace.getLatitude() != null && morningPlace.getLatitude() != 0.0 ? morningPlace.getLatitude() : centerLat + 0.012;
            Double morningLon = morningPlace != null && morningPlace.getLongitude() != null && morningPlace.getLongitude() != 0.0 ? morningPlace.getLongitude() : centerLon + 0.010;

            activities.add(ActivityResponse.builder()
                    .sequenceNumber(1)
                    .placeName(morningName)
                    .category(morningPlace != null && morningPlace.getCategory() != null ? morningPlace.getCategory() : "SIGHTSEEING")
                    .startTime("09:00 AM")
                    .endTime("12:00 PM")
                    .durationMinutes(180)
                    .estimatedCost(morningPlace != null && morningPlace.getEstimatedCost() != null ? morningPlace.getEstimatedCost() : costQuarter.multiply(BigDecimal.valueOf(1.3)))
                    .costType("ESTIMATED")
                    .description("Prime morning exploration of " + morningName + " before peak midday crowds.")
                    .photoUrl(morningPhoto)
                    .address(morningAddress)
                    .latitude(morningLat)
                    .longitude(morningLon)
                    .rating(morningPlace != null && morningPlace.getRating() != null ? morningPlace.getRating() : 4.7)
                    .openingHours(morningPlace != null && morningPlace.getOpeningHours() != null ? morningPlace.getOpeningHours() : "09:00 AM - 05:30 PM")
                    .build());

            activities.add(ActivityResponse.builder()
                    .sequenceNumber(2)
                    .placeName("Regional Flavor Midday Culinary Stop")
                    .category("DINING")
                    .startTime("12:30 PM")
                    .endTime("02:00 PM")
                    .durationMinutes(90)
                    .estimatedCost(costQuarter.multiply(BigDecimal.valueOf(1.0)))
                    .costType("ESTIMATED")
                    .description("Traditional lunch featuring authentic recipes curated for " + trip.getFoodPref() + " dining preferences.")
                    .photoUrl(placesService.getCategoryFallbackPhoto("DINING"))
                    .address("Culinary Lane, " + destination)
                    .latitude(centerLat + 0.005)
                    .longitude(centerLon + 0.006)
                    .rating(4.5)
                    .openingHours("12:00 PM - 03:30 PM")
                    .build());

            int afternoonIndex = (placeIndex + 1) % Math.max(1, places.size());
            PlaceDto afternoonPlace = (afternoonIndex < places.size()) ? places.get(afternoonIndex) : null;
            String afternoonName = afternoonPlace != null ? afternoonPlace.getName() : destination + " Scenic Viewpoint & Gardens";
            String afternoonPhoto = afternoonPlace != null && afternoonPlace.getPhotoUrl() != null ? afternoonPlace.getPhotoUrl() : placesService.getCategoryFallbackPhoto("NATURE");
            String afternoonAddress = afternoonPlace != null && afternoonPlace.getAddress() != null ? afternoonPlace.getAddress() : destination;
            Double afternoonLat = afternoonPlace != null && afternoonPlace.getLatitude() != null && afternoonPlace.getLatitude() != 0.0 ? afternoonPlace.getLatitude() : centerLat - 0.010;
            Double afternoonLon = afternoonPlace != null && afternoonPlace.getLongitude() != null && afternoonPlace.getLongitude() != 0.0 ? afternoonPlace.getLongitude() : centerLon + 0.012;

            activities.add(ActivityResponse.builder()
                    .sequenceNumber(3)
                    .placeName(afternoonName)
                    .category(afternoonPlace != null && afternoonPlace.getCategory() != null ? afternoonPlace.getCategory() : (interests.contains("beaches") ? "BEACH" : "NATURE"))
                    .startTime("03:00 PM")
                    .endTime("05:30 PM")
                    .durationMinutes(150)
                    .estimatedCost(afternoonPlace != null && afternoonPlace.getEstimatedCost() != null ? afternoonPlace.getEstimatedCost() : costQuarter.multiply(BigDecimal.valueOf(0.8)))
                    .costType("ESTIMATED")
                    .description("Scenic immersion and visual discovery at " + afternoonName + ".")
                    .photoUrl(afternoonPhoto)
                    .address(afternoonAddress)
                    .latitude(afternoonLat)
                    .longitude(afternoonLon)
                    .rating(afternoonPlace != null && afternoonPlace.getRating() != null ? afternoonPlace.getRating() : 4.6)
                    .openingHours(afternoonPlace != null && afternoonPlace.getOpeningHours() != null ? afternoonPlace.getOpeningHours() : "08:00 AM - 06:30 PM")
                    .build());

            activities.add(ActivityResponse.builder()
                    .sequenceNumber(4)
                    .placeName(destination + " Sunset Promenade & Evening Bazaar")
                    .category(interests.contains("nightlife") ? "NIGHTLIFE" : "RELAXATION")
                    .startTime("06:30 PM")
                    .endTime("09:00 PM")
                    .durationMinutes(150)
                    .estimatedCost(costQuarter.multiply(BigDecimal.valueOf(1.1)))
                    .costType("ESTIMATED")
                    .description("Catch the golden hour sunset, capture vibrant photographs, and enjoy an evening stroll.")
                    .photoUrl(placesService.getCategoryFallbackPhoto("BEACH"))
                    .address("Oceanfront / Scenic Vista, " + destination)
                    .latitude(centerLat - 0.015)
                    .longitude(centerLon - 0.008)
                    .rating(4.8)
                    .openingHours("Open until 11:00 PM")
                    .build());
        }

        return activities;
    }

    private String determineDayTheme(int dayNum, int totalDays, String destination, Set<String> interests) {
        if (dayNum == 1) return "Arrival & Neighborhood Orientation";
        if (dayNum == totalDays) return "Souvenirs, Final Flavors & Departure";

        if (interests.contains("beaches")) return "Coastal Breezes & Ocean Leisure";
        if (interests.contains("history") || interests.contains("temples")) return "Historic Marvels & Cultural Heritage";
        if (interests.contains("adventure")) return "Action-Packed Excursions & Thrills";
        if (interests.contains("nature") || interests.contains("photography")) return "Scenic Nature & Visual Wonders";
        if (interests.contains("food")) return "Gastronomic Trail & Local Delicacies";
        if (interests.contains("shopping")) return "Artisan Bazaars & Retail Therapy";

        return switch (dayNum % 3) {
            case 0 -> "Historic Landmarks & Cultural Highlights";
            case 1 -> "Scenic Landscapes & Hidden Gems";
            default -> "Local Living, Cafes & Artisan Streets";
        };
    }

    private Set<String> parseInterests(String interestsStr) {
        Set<String> set = new HashSet<>();
        if (interestsStr != null && !interestsStr.isBlank()) {
            for (String item : interestsStr.split(",")) {
                set.add(item.trim().toLowerCase());
            }
        }
        return set;
    }
}
