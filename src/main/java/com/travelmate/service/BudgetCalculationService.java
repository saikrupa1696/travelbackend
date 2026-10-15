package com.travelmate.service;

import com.travelmate.dto.itinerary.BudgetBreakdownResponse;
import com.travelmate.entity.Trip;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class BudgetCalculationService {

    public BudgetBreakdownResponse calculateBudgetBreakdown(
            Trip trip, BigDecimal totalActivitiesCost) {

        int days = Math.max(1, trip.getDurationDays());
        int travellers = Math.max(1, trip.getTravellerCount());

        // Standardize total and per-person budgets
        BigDecimal totalBudget;
        BigDecimal perPersonBudget;

        if ("PER_PERSON".equalsIgnoreCase(trip.getBudgetMode())) {
            perPersonBudget = trip.getBudgetAmount();
            totalBudget = perPersonBudget.multiply(BigDecimal.valueOf(travellers));
        } else {
            totalBudget = trip.getBudgetAmount();
            perPersonBudget = totalBudget.divide(BigDecimal.valueOf(travellers), 2, RoundingMode.HALF_UP);
        }

        // Tier multipliers based on accommodation preference
        double stayFactor = switch (trip.getAccommodationPref().toUpperCase()) {
            case "HOSTEL" -> 0.20;
            case "BUDGET" -> 0.25;
            case "LUXURY", "RESORT" -> 0.45;
            default -> 0.35; // STANDARD
        };

        // Food factor based on preference
        double foodFactor = switch (trip.getFoodPref().toUpperCase()) {
            case "LOCAL_STREET" -> 0.15;
            case "FINE_DINING" -> 0.30;
            default -> 0.22; // STANDARD / VEG / ANY
        };

        // Transport factor
        double transportFactor = 0.15;

        // Activity factor
        double defaultActivityFactor = 0.18;
        double miscFactor = 0.10;

        // Calculate estimated amounts
        BigDecimal accommodationEst = totalBudget.multiply(BigDecimal.valueOf(stayFactor))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal foodEst = totalBudget.multiply(BigDecimal.valueOf(foodFactor))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal transportEst = totalBudget.multiply(BigDecimal.valueOf(transportFactor))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal activitiesEst = (totalActivitiesCost != null && totalActivitiesCost.compareTo(BigDecimal.ZERO) > 0)
                ? totalActivitiesCost
                : totalBudget.multiply(BigDecimal.valueOf(defaultActivityFactor)).setScale(2, RoundingMode.HALF_UP);

        BigDecimal miscEst = totalBudget.multiply(BigDecimal.valueOf(miscFactor))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalEstimatedCost = accommodationEst
                .add(foodEst)
                .add(transportEst)
                .add(activitiesEst)
                .add(miscEst);

        BigDecimal remainingBudget = totalBudget.subtract(totalEstimatedCost);
        BigDecimal perPersonEstimatedCost = totalEstimatedCost.divide(BigDecimal.valueOf(travellers), 2, RoundingMode.HALF_UP);
        BigDecimal dailyEstimatedSpending = totalEstimatedCost.divide(BigDecimal.valueOf(days), 2, RoundingMode.HALF_UP);

        double percentageUsed = totalBudget.compareTo(BigDecimal.ZERO) > 0
                ? totalEstimatedCost.multiply(BigDecimal.valueOf(100)).divide(totalBudget, 1, RoundingMode.HALF_UP).doubleValue()
                : 0.0;

        String budgetStatus;
        String advice;

        if (remainingBudget.compareTo(BigDecimal.ZERO) < 0) {
            budgetStatus = "OVER_BUDGET";
            advice = String.format("Estimated expenses exceed your allocated budget by ₹%s (%.1f%% of budget used). Consider selecting standard accommodation or trimming paid excursions to remain within target.",
                    remainingBudget.abs(), percentageUsed);
        } else if (percentageUsed > 90.0 || remainingBudget.compareTo(totalBudget.multiply(BigDecimal.valueOf(0.10))) < 0) {
            budgetStatus = "TIGHT_BUDGET";
            advice = String.format("Your budget is getting tight with %.1f%% committed (₹%s safety cushion remaining). Keep track of discretionary dining and shopping.",
                    percentageUsed, remainingBudget);
        } else {
            budgetStatus = "WITHIN_BUDGET";
            advice = String.format("Budget is comfortable! You have an uncommitted buffer of ₹%s (%.1f%% utilized) for souvenirs, spontaneous activities, or emergency reserves.",
                    remainingBudget, percentageUsed);
        }

        String disclaimer = "Estimates reflect local market rates for stays, meals, local transit, and verified entry fees. Inter-city flights/trains and personal shopping are excluded. Prices are realistic estimates and not guaranteed bookings.";

        return BudgetBreakdownResponse.builder()
                .totalBudget(totalBudget)
                .totalEstimatedCost(totalEstimatedCost)
                .remainingBudget(remainingBudget)
                .budgetMode(trip.getBudgetMode())
                .travellerCount(travellers)
                .perPersonBudget(perPersonBudget)
                .perPersonEstimatedCost(perPersonEstimatedCost)
                .accommodationEst(accommodationEst)
                .foodEst(foodEst)
                .transportEst(transportEst)
                .activitiesEst(activitiesEst)
                .miscEst(miscEst)
                .budgetStatus(budgetStatus)
                .advice(advice)
                .percentageUsed(percentageUsed)
                .dailyEstimatedSpending(dailyEstimatedSpending)
                .disclaimer(disclaimer)
                .currency("INR")
                .build();
    }
}
