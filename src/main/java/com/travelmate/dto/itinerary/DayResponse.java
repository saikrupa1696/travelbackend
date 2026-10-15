package com.travelmate.dto.itinerary;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DayResponse {
    private Long id;
    private Integer dayNumber;
    private LocalDate date;
    private String theme;
    private BigDecimal dailyBudget = BigDecimal.ZERO;
    private BigDecimal dailyEstimatedCost = BigDecimal.ZERO;
    private List<ActivityResponse> activities = new ArrayList<>();

    public DayResponse() {}

    public DayResponse(Long id, Integer dayNumber, LocalDate date, String theme,
                       BigDecimal dailyBudget, BigDecimal dailyEstimatedCost, List<ActivityResponse> activities) {
        this.id = id;
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
        private Integer dayNumber;
        private LocalDate date;
        private String theme;
        private BigDecimal dailyBudget = BigDecimal.ZERO;
        private BigDecimal dailyEstimatedCost = BigDecimal.ZERO;
        private List<ActivityResponse> activities = new ArrayList<>();

        public Builder id(Long id) { this.id = id; return this; }
        public Builder dayNumber(Integer dayNumber) { this.dayNumber = dayNumber; return this; }
        public Builder date(LocalDate date) { this.date = date; return this; }
        public Builder theme(String theme) { this.theme = theme; return this; }
        public Builder dailyBudget(BigDecimal dailyBudget) { this.dailyBudget = dailyBudget; return this; }
        public Builder dailyEstimatedCost(BigDecimal dailyEstimatedCost) { this.dailyEstimatedCost = dailyEstimatedCost; return this; }
        public Builder activities(List<ActivityResponse> activities) { this.activities = activities; return this; }
        public DayResponse build() {
            return new DayResponse(id, dayNumber, date, theme, dailyBudget, dailyEstimatedCost, activities);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public List<ActivityResponse> getActivities() { return activities; }
    public void setActivities(List<ActivityResponse> activities) { this.activities = activities; }
}
