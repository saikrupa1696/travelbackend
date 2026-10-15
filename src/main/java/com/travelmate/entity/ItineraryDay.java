package com.travelmate.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "itinerary_days")
public class ItineraryDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "itinerary_id", nullable = false)
    private Itinerary itinerary;

    @Column(name = "day_number", nullable = false)
    private Integer dayNumber;

    @Column
    private LocalDate date;

    @Column
    private String theme;

    @Column(name = "daily_budget", precision = 10, scale = 2)
    private BigDecimal dailyBudget = BigDecimal.ZERO;

    @Column(name = "daily_estimated_cost", precision = 10, scale = 2)
    private BigDecimal dailyEstimatedCost = BigDecimal.ZERO;

    @OneToMany(mappedBy = "itineraryDay", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("sequenceNumber ASC")
    private List<ItineraryActivity> activities = new ArrayList<>();

    public ItineraryDay() {}

    public ItineraryDay(Long id, Itinerary itinerary, Integer dayNumber, LocalDate date, String theme,
                        BigDecimal dailyBudget, BigDecimal dailyEstimatedCost, List<ItineraryActivity> activities) {
        this.id = id;
        this.itinerary = itinerary;
        this.dayNumber = dayNumber;
        this.date = date;
        this.theme = theme;
        this.dailyBudget = dailyBudget != null ? dailyBudget : BigDecimal.ZERO;
        this.dailyEstimatedCost = dailyEstimatedCost != null ? dailyEstimatedCost : BigDecimal.ZERO;
        this.activities = activities != null ? activities : new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Itinerary itinerary;
        private Integer dayNumber;
        private LocalDate date;
        private String theme;
        private BigDecimal dailyBudget = BigDecimal.ZERO;
        private BigDecimal dailyEstimatedCost = BigDecimal.ZERO;
        private List<ItineraryActivity> activities = new ArrayList<>();

        public Builder id(Long id) { this.id = id; return this; }
        public Builder itinerary(Itinerary itinerary) { this.itinerary = itinerary; return this; }
        public Builder dayNumber(Integer dayNumber) { this.dayNumber = dayNumber; return this; }
        public Builder date(LocalDate date) { this.date = date; return this; }
        public Builder theme(String theme) { this.theme = theme; return this; }
        public Builder dailyBudget(BigDecimal dailyBudget) { this.dailyBudget = dailyBudget; return this; }
        public Builder dailyEstimatedCost(BigDecimal dailyEstimatedCost) { this.dailyEstimatedCost = dailyEstimatedCost; return this; }
        public Builder activities(List<ItineraryActivity> activities) { this.activities = activities; return this; }
        public ItineraryDay build() {
            return new ItineraryDay(id, itinerary, dayNumber, date, theme, dailyBudget, dailyEstimatedCost, activities);
        }
    }

    public void addActivity(ItineraryActivity activity) {
        activities.add(activity);
        activity.setItineraryDay(this);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Itinerary getItinerary() { return itinerary; }
    public void setItinerary(Itinerary itinerary) { this.itinerary = itinerary; }
    public Integer getDayNumber() { return dayNumber; }
    public void setDayNumber(Integer dayNumber) { this.dayNumber = dayNumber; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }
    public BigDecimal getDailyBudget() { return dailyBudget; }
    public void setDailyBudget(BigDecimal dailyBudget) { this.dailyBudget = dailyBudget; }
    public BigDecimal getDailyEstimatedCost() { return dailyEstimatedCost; }
    public void setDailyEstimatedCost(BigDecimal dailyEstimatedCost) { this.dailyEstimatedCost = dailyEstimatedCost; }
    public List<ItineraryActivity> getActivities() { return activities; }
    public void setActivities(List<ItineraryActivity> activities) { this.activities = activities; }
}
