package com.travelmate.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "itineraries")
public class Itinerary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(name = "generation_source", nullable = false, length = 50)
    private String generationSource = "RULE_BASED";

    @Column(name = "total_estimated_cost", precision = 12, scale = 2)
    private BigDecimal totalEstimatedCost = BigDecimal.ZERO;

    @Column(name = "remaining_budget", precision = 12, scale = 2)
    private BigDecimal remainingBudget = BigDecimal.ZERO;

    @Column(name = "budget_status", length = 50)
    private String budgetStatus = "WITHIN_BUDGET";

    @Column(name = "itinerary_data", columnDefinition = "LONGTEXT")
    private String itineraryData;

    @OneToMany(mappedBy = "itinerary", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("dayNumber ASC")
    private List<ItineraryDay> days = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Itinerary() {}

    public Itinerary(Long id, Trip trip, String title, String summary, String generationSource,
                     BigDecimal totalEstimatedCost, BigDecimal remainingBudget, String budgetStatus,
                     String itineraryData, List<ItineraryDay> days, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.trip = trip;
        this.title = title;
        this.summary = summary;
        this.generationSource = generationSource != null ? generationSource : "RULE_BASED";
        this.totalEstimatedCost = totalEstimatedCost != null ? totalEstimatedCost : BigDecimal.ZERO;
        this.remainingBudget = remainingBudget != null ? remainingBudget : BigDecimal.ZERO;
        this.budgetStatus = budgetStatus != null ? budgetStatus : "WITHIN_BUDGET";
        this.itineraryData = itineraryData;
        this.days = days != null ? days : new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Trip trip;
        private String title;
        private String summary;
        private String generationSource = "RULE_BASED";
        private BigDecimal totalEstimatedCost = BigDecimal.ZERO;
        private BigDecimal remainingBudget = BigDecimal.ZERO;
        private String budgetStatus = "WITHIN_BUDGET";
        private String itineraryData;
        private List<ItineraryDay> days = new ArrayList<>();
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder trip(Trip trip) { this.trip = trip; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder summary(String summary) { this.summary = summary; return this; }
        public Builder generationSource(String generationSource) { this.generationSource = generationSource; return this; }
        public Builder totalEstimatedCost(BigDecimal totalEstimatedCost) { this.totalEstimatedCost = totalEstimatedCost; return this; }
        public Builder remainingBudget(BigDecimal remainingBudget) { this.remainingBudget = remainingBudget; return this; }
        public Builder budgetStatus(String budgetStatus) { this.budgetStatus = budgetStatus; return this; }
        public Builder itineraryData(String itineraryData) { this.itineraryData = itineraryData; return this; }
        public Builder days(List<ItineraryDay> days) { this.days = days; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public Itinerary build() {
            return new Itinerary(id, trip, title, summary, generationSource, totalEstimatedCost,
                    remainingBudget, budgetStatus, itineraryData, days, createdAt, updatedAt);
        }
    }

    public void addDay(ItineraryDay day) {
        days.add(day);
        day.setItinerary(this);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Trip getTrip() { return trip; }
    public void setTrip(Trip trip) { this.trip = trip; }
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
    public String getItineraryData() { return itineraryData; }
    public void setItineraryData(String itineraryData) { this.itineraryData = itineraryData; }
    public List<ItineraryDay> getDays() { return days; }
    public void setDays(List<ItineraryDay> days) { this.days = days; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
