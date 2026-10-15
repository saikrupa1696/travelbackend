package com.travelmate.dto.itinerary;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ItineraryResponse {
    private Long id;
    private Long tripId;
    private String destination;
    private String title;
    private String summary;
    private String generationSource = "RULE_BASED";
    private BigDecimal totalEstimatedCost = BigDecimal.ZERO;
    private BigDecimal remainingBudget = BigDecimal.ZERO;
    private String budgetStatus = "WITHIN_BUDGET";
    private List<DayResponse> days = new ArrayList<>();
    private BudgetBreakdownResponse budgetBreakdown;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ItineraryResponse() {}

    public ItineraryResponse(Long id, Long tripId, String destination, String title, String summary,
                             String generationSource, BigDecimal totalEstimatedCost, BigDecimal remainingBudget,
                             String budgetStatus, List<DayResponse> days, BudgetBreakdownResponse budgetBreakdown,
                             LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.tripId = tripId;
        this.destination = destination;
        this.title = title;
        this.summary = summary;
        this.generationSource = generationSource != null ? generationSource : "RULE_BASED";
        this.totalEstimatedCost = totalEstimatedCost != null ? totalEstimatedCost : BigDecimal.ZERO;
        this.remainingBudget = remainingBudget != null ? remainingBudget : BigDecimal.ZERO;
        this.budgetStatus = budgetStatus != null ? budgetStatus : "WITHIN_BUDGET";
        this.days = days != null ? days : new ArrayList<>();
        this.budgetBreakdown = budgetBreakdown;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long tripId;
        private String destination;
        private String title;
        private String summary;
        private String generationSource = "RULE_BASED";
        private BigDecimal totalEstimatedCost = BigDecimal.ZERO;
        private BigDecimal remainingBudget = BigDecimal.ZERO;
        private String budgetStatus = "WITHIN_BUDGET";
        private List<DayResponse> days = new ArrayList<>();
        private BudgetBreakdownResponse budgetBreakdown;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder tripId(Long tripId) { this.tripId = tripId; return this; }
        public Builder destination(String destination) { this.destination = destination; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder summary(String summary) { this.summary = summary; return this; }
        public Builder generationSource(String generationSource) { this.generationSource = generationSource; return this; }
        public Builder totalEstimatedCost(BigDecimal totalEstimatedCost) { this.totalEstimatedCost = totalEstimatedCost; return this; }
        public Builder remainingBudget(BigDecimal remainingBudget) { this.remainingBudget = remainingBudget; return this; }
        public Builder budgetStatus(String budgetStatus) { this.budgetStatus = budgetStatus; return this; }
        public Builder days(List<DayResponse> days) { this.days = days; return this; }
        public Builder budgetBreakdown(BudgetBreakdownResponse budgetBreakdown) { this.budgetBreakdown = budgetBreakdown; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public ItineraryResponse build() {
            return new ItineraryResponse(id, tripId, destination, title, summary, generationSource,
                    totalEstimatedCost, remainingBudget, budgetStatus, days, budgetBreakdown, createdAt, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTripId() { return tripId; }
    public void setTripId(Long tripId) { this.tripId = tripId; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getGenerationSource() { return generationSource; }
    public void setGenerationSource(String generationSource) { this.generationSource = generationSource; }
    public BigDecimal getTotalEstimatedCost() { return totalEstimatedCost; }
    public void setTotalEstimatedCost(BigDecimal totalEstimatedCost) { this.totalEstimatedCost = totalEstimatedCost; }
    public BigDecimal getRemainingBudget() { return remainingBudget; }
    public void setRemainingBudget(BigDecimal remainingBudget) { this.remainingBudget = remainingBudget; }
    public String getBudgetStatus() { return budgetStatus; }
    public void setBudgetStatus(String budgetStatus) { this.budgetStatus = budgetStatus; }
    public List<DayResponse> getDays() { return days; }
    public void setDays(List<DayResponse> days) { this.days = days; }
    public BudgetBreakdownResponse getBudgetBreakdown() { return budgetBreakdown; }
    public void setBudgetBreakdown(BudgetBreakdownResponse budgetBreakdown) { this.budgetBreakdown = budgetBreakdown; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
