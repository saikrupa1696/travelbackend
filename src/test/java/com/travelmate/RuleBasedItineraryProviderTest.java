package com.travelmate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelmate.dto.itinerary.ItineraryResponse;
import com.travelmate.entity.Trip;
import com.travelmate.service.BudgetCalculationService;
import com.travelmate.service.PlacesService;
import com.travelmate.service.ai.RuleBasedItineraryProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class RuleBasedItineraryProviderTest {

    private RuleBasedItineraryProvider provider;

    @BeforeEach
    void setUp() {
        BudgetCalculationService budgetService = new BudgetCalculationService();
        PlacesService placesService = new PlacesService(new RestTemplate(), new ObjectMapper(), "TestAgent/1.0");
        provider = new RuleBasedItineraryProvider(placesService, budgetService);
    }

    @Test
    void testGenerateItineraryForTrip() {
        Trip trip = Trip.builder()
                .id(1L)
                .destination("Paris")
                .startDate(LocalDate.now().plusDays(10))
                .durationDays(3)
                .travelType("Couple")
                .travellerCount(2)
                .budgetAmount(new BigDecimal("1500.00"))
                .budgetMode("TOTAL")
                .interests("Culture, Food, History")
                .travelPace("MODERATE")
                .accommodationPref("STANDARD")
                .foodPref("ANY")
                .build();

        ItineraryResponse itinerary = provider.generateItinerary(trip);

        assertNotNull(itinerary);
        assertEquals("Paris", itinerary.getDestination());
        assertEquals("RULE_BASED", itinerary.getGenerationSource());
        assertEquals(3, itinerary.getDays().size());
        assertFalse(itinerary.getDays().get(0).getActivities().isEmpty());
        assertNotNull(itinerary.getBudgetBreakdown());
    }
}
