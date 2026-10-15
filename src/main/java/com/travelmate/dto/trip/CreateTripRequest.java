package com.travelmate.dto.trip;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateTripRequest {

    @NotBlank(message = "Destination is required")
    @Size(max = 200, message = "Destination cannot exceed 200 characters")
    private String destination;
    private Double destinationLatitude;
    private Double destinationLongitude;
    private String destinationPlaceId;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be today or in the future")
    private LocalDate startDate;

    @NotNull(message = "Duration in days is required")
    @Min(value = 1, message = "Trip must be at least 1 day")
    @Max(value = 30, message = "Trip cannot exceed 30 days")
    private Integer durationDays;

    @NotBlank(message = "Travel type is required")
    private String travelType;

    @NotNull(message = "Number of travellers is required")
    @Min(value = 1, message = "Traveller count must be at least 1")
    @Max(value = 50, message = "Traveller count cannot exceed 50")
    private Integer travellerCount;

    @NotNull(message = "Budget amount is required")
    @DecimalMin(value = "100.00", message = "Budget must be at least 100")
    private BigDecimal budgetAmount;

    @NotBlank(message = "Budget mode is required")
    private String budgetMode = "TOTAL";

    private String interests;
    private String travelPace = "MODERATE";
    private String accommodationPref = "STANDARD";
    private String foodPref = "ANY";
    private String additionalNotes;

    public CreateTripRequest() {}

    public CreateTripRequest(String destination, Double destinationLatitude, Double destinationLongitude,
                             String destinationPlaceId, LocalDate startDate, Integer durationDays, String travelType,
                             Integer travellerCount, BigDecimal budgetAmount, String budgetMode, String interests,
                             String travelPace, String accommodationPref, String foodPref, String additionalNotes) {
        this.destination = destination;
        this.destinationLatitude = destinationLatitude;
        this.destinationLongitude = destinationLongitude;
        this.destinationPlaceId = destinationPlaceId;
        this.startDate = startDate;
        this.durationDays = durationDays;
        this.travelType = travelType;
        this.travellerCount = travellerCount;
        this.budgetAmount = budgetAmount;
        this.budgetMode = budgetMode != null ? budgetMode : "TOTAL";
        this.interests = interests;
        this.travelPace = travelPace != null ? travelPace : "MODERATE";
        this.accommodationPref = accommodationPref != null ? accommodationPref : "STANDARD";
        this.foodPref = foodPref != null ? foodPref : "ANY";
        this.additionalNotes = additionalNotes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String destination;
        private Double destinationLatitude;
        private Double destinationLongitude;
        private String destinationPlaceId;
        private LocalDate startDate;
        private Integer durationDays;
        private String travelType;
        private Integer travellerCount;
        private BigDecimal budgetAmount;
        private String budgetMode = "TOTAL";
        private String interests;
        private String travelPace = "MODERATE";
        private String accommodationPref = "STANDARD";
        private String foodPref = "ANY";
        private String additionalNotes;

        public Builder destination(String destination) { this.destination = destination; return this; }
        public Builder destinationLatitude(Double destinationLatitude) { this.destinationLatitude = destinationLatitude; return this; }
        public Builder destinationLongitude(Double destinationLongitude) { this.destinationLongitude = destinationLongitude; return this; }
        public Builder destinationPlaceId(String destinationPlaceId) { this.destinationPlaceId = destinationPlaceId; return this; }
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
        public CreateTripRequest build() {
            return new CreateTripRequest(destination, destinationLatitude, destinationLongitude, destinationPlaceId,
                    startDate, durationDays, travelType, travellerCount, budgetAmount, budgetMode, interests,
                    travelPace, accommodationPref, foodPref, additionalNotes);
        }
    }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public Double getDestinationLatitude() { return destinationLatitude; }
    public void setDestinationLatitude(Double destinationLatitude) { this.destinationLatitude = destinationLatitude; }
    public Double getDestinationLongitude() { return destinationLongitude; }
    public void setDestinationLongitude(Double destinationLongitude) { this.destinationLongitude = destinationLongitude; }
    public String getDestinationPlaceId() { return destinationPlaceId; }
    public void setDestinationPlaceId(String destinationPlaceId) { this.destinationPlaceId = destinationPlaceId; }
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
