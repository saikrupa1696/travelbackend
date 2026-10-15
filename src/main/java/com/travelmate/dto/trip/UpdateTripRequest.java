package com.travelmate.dto.trip;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class UpdateTripRequest {

    @NotBlank(message = "Destination is required")
    private String destination;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "Duration in days is required")
    @Min(1)
    @Max(30)
    private Integer durationDays;

    @NotBlank(message = "Travel type is required")
    private String travelType;

    @NotNull(message = "Number of travellers is required")
    @Min(1)
    private Integer travellerCount;

    @NotNull(message = "Budget amount is required")
    @DecimalMin("100.00")
    private BigDecimal budgetAmount;

    @NotBlank
    private String budgetMode;

    private String interests;
    private String travelPace;
    private String accommodationPref;
    private String foodPref;
    private String additionalNotes;

    public UpdateTripRequest() {}

    public UpdateTripRequest(String destination, LocalDate startDate, Integer durationDays, String travelType,
                             Integer travellerCount, BigDecimal budgetAmount, String budgetMode, String interests,
                             String travelPace, String accommodationPref, String foodPref, String additionalNotes) {
        this.destination = destination;
        this.startDate = startDate;
        this.durationDays = durationDays;
        this.travelType = travelType;
        this.travellerCount = travellerCount;
        this.budgetAmount = budgetAmount;
        this.budgetMode = budgetMode;
        this.interests = interests;
        this.travelPace = travelPace;
        this.accommodationPref = accommodationPref;
        this.foodPref = foodPref;
        this.additionalNotes = additionalNotes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String destination;
        private LocalDate startDate;
        private Integer durationDays;
        private String travelType;
        private Integer travellerCount;
        private BigDecimal budgetAmount;
        private String budgetMode;
        private String interests;
        private String travelPace;
        private String accommodationPref;
        private String foodPref;
        private String additionalNotes;

        public Builder destination(String destination) { this.destination = destination; return this; }
        public Builder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public Builder durationDays(Integer durationDays) { this.durationDays = durationDays; return this; }
        public Builder travelType(String travelType) { this.travelType = travelType; return this; }
        public Builder travellerCount(Integer travellerCount) { this.travellerCount = travellerCount; return this; }
        public Builder budgetAmount(BigDecimal budgetAmount) { this.budgetAmount = budgetAmount; return this; }
        public Builder budgetMode(String budgetMode) { this.budgetMode = budgetMode; return this; }
        public Builder interests(String interests) { this.interests = interests; return this; }
        public Builder travelPace(String travelPace) { this.travelPace = travelPace; return this; }
        public Builder accommodationPref(String accommodationPref) { this.accommodationPref = accommodationPref; return this; }
        public Builder foodPref(String foodPref) { this.foodPref = foodPref; return this; }
        public Builder additionalNotes(String additionalNotes) { this.additionalNotes = additionalNotes; return this; }
        public UpdateTripRequest build() {
            return new UpdateTripRequest(destination, startDate, durationDays, travelType, travellerCount,
                    budgetAmount, budgetMode, interests, travelPace, accommodationPref, foodPref, additionalNotes);
        }
    }

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
    public String getInterests() { return interests; }
    public void setInterests(String interests) { this.interests = interests; }
    public String getTravelPace() { return travelPace; }
    public void setTravelPace(String travelPace) { this.travelPace = travelPace; }
    public String getAccommodationPref() { return accommodationPref; }
    public void setAccommodationPref(String accommodationPref) { this.accommodationPref = accommodationPref; }
    public String getFoodPref() { return foodPref; }
    public void setFoodPref(String foodPref) { this.foodPref = foodPref; }
    public String getAdditionalNotes() { return additionalNotes; }
    public void setAdditionalNotes(String additionalNotes) { this.additionalNotes = additionalNotes; }
}
