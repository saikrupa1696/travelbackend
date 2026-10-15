package com.travelmate;

import com.travelmate.dto.itinerary.BudgetBreakdownResponse;
import com.travelmate.entity.Trip;
import com.travelmate.service.BudgetCalculationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BudgetCalculationServiceTest {

    private BudgetCalculationService budgetCalculationService;

    @BeforeEach
    void setUp() {
        budgetCalculationService = new BudgetCalculationService();
    }

    @Test
    void testTotalBudgetCalculationWithinBudget() {
        Trip trip = Trip.builder()
                .durationDays(5)
                .travellerCount(2)
                .budgetAmount(new BigDecimal("3000.00"))
                .budgetMode("TOTAL")
                .accommodationPref("STANDARD")
                .foodPref("ANY")
                .build();

        BudgetBreakdownResponse result = budgetCalculationService.calculateBudgetBreakdown(trip, new BigDecimal("200.00"));

        assertNotNull(result);
        assertEquals(new BigDecimal("3000.00"), result.getTotalBudget());
        assertEquals(new BigDecimal("1500.00"), result.getPerPersonBudget());
        assertTrue(result.getAccommodationEst().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(result.getFoodEst().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(result.getTransportEst().compareTo(BigDecimal.ZERO) > 0);
        assertEquals("WITHIN_BUDGET", result.getBudgetStatus());
    }

    @Test
    void testPerPersonBudgetCalculation() {
        Trip trip = Trip.builder()
                .durationDays(4)
                .travellerCount(3)
                .budgetAmount(new BigDecimal("800.00"))
                .budgetMode("PER_PERSON")
                .accommodationPref("BUDGET")
                .foodPref("LOCAL_STREET")
                .build();

        BudgetBreakdownResponse result = budgetCalculationService.calculateBudgetBreakdown(trip, new BigDecimal("200.00"));

        assertNotNull(result);
        assertEquals(new BigDecimal("2400.00"), result.getTotalBudget());
        assertEquals(new BigDecimal("800.00"), result.getPerPersonBudget());
    }
}
