package com.travelmate.dto.trip;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TripSummaryResponse {
    private Long id;
    private String destination;
    private LocalDate startDate;
    private Integer durationDays;
    private String travelType;
    private Integer travellerCount;
    private BigDecimal budgetAmount;
    private String budgetMode;
    private boolean hasItinerary;
    private String generationSource;
    private BigDecimal totalEstimatedCost;
    private LocalDateTime createdAt;

    public TripSummaryResponse() {}

    public TripSummaryResponse(Long id, String destination, LocalDate startDate, Integer durationDays,
                               String travelType, Integer travellerCount, BigDecimal budgetAmount, String budgetMode,
                               boolean hasItinerary, String generationSource, BigDecimal totalEstimatedCost, LocalDateTime createdAt) {
        this.id = id;
        this.destination = destination;
        this.startDate = startDate;
        this.durationDays = durationDays;
        this.travelType = travelType;
        this.travellerCount = travellerCount;
        this.budgetAmount = budgetAmount;
        this.budgetMode = budgetMode;
        this.hasItinerary = hasItinerary;
        this.generationSource = generationSource;
        this.totalEstimatedCost = totalEstimatedCost;
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String destination;
        private LocalDate startDate;
        private Integer durationDays;
        private String travelType;
        private Integer travellerCount;
        private BigDecimal budgetAmount;
        private String budgetMode;
        private boolean hasItinerary;
        private String generationSource;
        private BigDecimal totalEstimatedCost;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder destination(String destination) { this.destination = destination; return this; }
        public Builder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public Builder durationDays(Integer durationDays) { this.durationDays = durationDays; return this; }
        public Builder travelType(String travelType) { this.travelType = travelType; return this; }
        public Builder travellerCount(Integer travellerCount) { this.travellerCount = travellerCount; return this; }
        public Builder budgetAmount(BigDecimal budgetAmount) { this.budgetAmount = budgetAmount; return this; }
        public Builder budgetMode(String budgetMode) { this.budgetMode = budgetMode; return this; }
        public Builder hasItinerary(boolean hasItinerary) { this.hasItinerary = hasItinerary; return this; }
        public Builder generationSource(String generationSource) { this.generationSource = generationSource; return this; }
        public Builder totalEstimatedCost(BigDecimal totalEstimatedCost) { this.totalEstimatedCost = totalEstimatedCost; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public TripSummaryResponse build() {
            return new TripSummaryResponse(id, destination, startDate, durationDays, travelType,
                    travellerCount, budgetAmount, budgetMode, hasItinerary, generationSource, totalEstimatedCost, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public Integer getDurationDays() { return durationDays; }
    public void setDurationDays(Integer durationDays) { this.durationDays = durationDays; }
    public String getTravelType() { return travelType; }
    public void setTravelType(String travelType) { this.travelType = travelType; }
    public Integer getTravellerCount() { return travellerCount; }
    public void setTravellerCount(Integer travellerCount) { this.travellerCount = travellerCount; }
    public BigDecimal getBudgetAmount() { return budgetAmount; }
    public void setBudgetAmount(BigDecimal budgetAmount) { this.budgetAmount = budgetAmount; }
    public String getBudgetMode() { return budgetMode; }
    public void setBudgetMode(String budgetMode) { this.budgetMode = budgetMode; }
    public boolean isHasItinerary() { return hasItinerary; }
    public void setHasItinerary(boolean hasItinerary) { this.hasItinerary = hasItinerary; }
    public String getGenerationSource() { return generationSource; }
    public void setGenerationSource(String generationSource) { this.generationSource = generationSource; }
    public BigDecimal getTotalEstimatedCost() { return totalEstimatedCost; }
    public void setTotalEstimatedCost(BigDecimal totalEstimatedCost) { this.totalEstimatedCost = totalEstimatedCost; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
